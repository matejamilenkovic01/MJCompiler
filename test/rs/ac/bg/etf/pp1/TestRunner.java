package rs.ac.bg.etf.pp1;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Pokretac testova za leksicku i sintaksnu analizu.
 *
 * Za svaki .mj fajl iz test foldera pokrece {@link Compiler} u zasebnom JVM procesu,
 * preusmerava standardni izlaz u istoimeni .out fajl a izlaz gresaka u .err fajl
 * (kako je opisano u postavci projekta), i proverava da li rezultat odgovara
 * ocekivanju zapisanom u samom .mj fajlu.
 *
 * Ocekivanje se pise kao komentar bilo gde u test fajlu:
 *
 * <pre>
 *   // EXPECT: OK
 *   // EXPECT: SYNTAX&gt;=2 RECOVERY&gt;=1 LEXICAL&gt;=0
 *   // EXPECT-ERR: deo poruke koji se mora pojaviti na stderr
 * </pre>
 *
 * OK znaci: nijedna greska, i prevodilac se zavrsio sa izlaznim kodom 0.
 * U suprotnom se trazi da parser NIJE prekinuo rad (oporavak je uspeo) i da su
 * zadovoljeni svi navedeni minimalni brojevi gresaka.
 *
 * Pokretanje iz Eclipse-a: Run As -> Java Application. Radni direktorijum mora
 * biti koren projekta (sto je podrazumevano).
 */
public class TestRunner {

	private static final String DEFAULT_TEST_DIR = "test";
	private static final long TIMEOUT_SECONDS = 30;
	private static final Charset CS = Charset.defaultCharset();

	/** Poruke po kojima se prepoznaju i broje pojedine vrste gresaka. */
	private static final String MARK_LEXICAL = "Leksicka greska";
	private static final String MARK_SYNTAX = "Sintaksna greska";
	private static final String MARK_RECOVERY = "Oporavak od greske";

	public static void main(String[] args) throws Exception {

		File testDir = new File(args.length > 0 ? args[0] : DEFAULT_TEST_DIR);

		if (!testDir.isDirectory()) {
			System.err.println("Test folder ne postoji: " + testDir.getAbsolutePath());
			System.exit(2);
		}

		File[] found = testDir.listFiles(new java.io.FilenameFilter() {
			public boolean accept(File dir, String name) {
				return name.toLowerCase().endsWith(".mj");
			}
		});

		if (found == null || found.length == 0) {
			System.err.println("Nije pronadjen nijedan .mj fajl u " + testDir.getAbsolutePath());
			System.exit(2);
		}

		Arrays.sort(found, new Comparator<File>() {
			public int compare(File a, File b) {
				return a.getName().compareTo(b.getName());
			}
		});

		System.out.println("Pokrecem " + found.length + " testova iz " + testDir.getPath());
		System.out.println();

		int passed = 0;
		List<String> failures = new ArrayList<String>();

		for (File mj : found) {
			Result r = runOne(mj);
			if (r.passed) {
				passed++;
				System.out.printf("  PROLAZ  %-28s %s%n", mj.getName(), r.summary);
			} else {
				System.out.printf("  PAD     %-28s %s%n", mj.getName(), r.summary);
				failures.add(mj.getName() + ": " + r.reason);
			}
		}

		System.out.println();
		System.out.println("------------------------------------------------------------");
		System.out.println("Ukupno: " + found.length + ", proslo: " + passed
				+ ", palo: " + (found.length - passed));

		if (!failures.isEmpty()) {
			System.out.println();
			System.out.println("Detalji neuspelih testova:");
			for (String f : failures) {
				System.out.println("  - " + f);
			}
			System.out.println();
			System.out.println("Za svaki test pogledaj pripadajuci .out i .err fajl.");
		}

		System.exit(failures.isEmpty() ? 0 : 1);
	}

	private static Result runOne(File mj) throws Exception {

		Expectation exp = Expectation.parse(mj);

		String base = mj.getPath().substring(0, mj.getPath().length() - 3);
		File outFile = new File(base + ".out");
		File errFile = new File(base + ".err");
		File objFile = new File(base + ".obj");

		int exitCode = runCompiler(mj, objFile, outFile, errFile);

		String err = readFile(errFile);

		int lexical = count(err, MARK_LEXICAL);
		int syntax = count(err, MARK_SYNTAX);
		int recovery = count(err, MARK_RECOVERY);

		String summary = "izlaz=" + exitCode + " leks=" + lexical
				+ " sint=" + syntax + " oporavak=" + recovery;

		Result r = new Result();
		r.summary = summary;

		if (exp == null) {
			r.passed = false;
			r.reason = "nedostaje '// EXPECT:' zaglavlje u test fajlu";
			return r;
		}

		if (exitCode == Compiler.EXIT_USAGE) {
			r.passed = false;
			r.reason = "prevodilac nije mogao da otvori ulazni fajl";
			return r;
		}

		if (exp.ok) {
			if (exitCode != Compiler.EXIT_OK) {
				r.passed = false;
				r.reason = "ocekivan ispravan program, ali je izlazni kod " + exitCode;
				return r;
			}
			if (lexical + syntax + recovery > 0) {
				r.passed = false;
				r.reason = "ocekivan ispravan program, ali su prijavljene greske (" + summary + ")";
				return r;
			}
			r.passed = true;
			return r;
		}

		// Neispravan program: kljucni zahtev je da se parsiranje NIJE prekinulo.
		if (exitCode == Compiler.EXIT_FATAL) {
			r.passed = false;
			r.reason = "oporavak nije uspeo - parsiranje je prekinuto pre kraja fajla";
			return r;
		}

		if (lexical < exp.minLexical) {
			r.passed = false;
			r.reason = "ocekivano najmanje " + exp.minLexical + " leksickih gresaka, a ima " + lexical;
			return r;
		}
		if (syntax < exp.minSyntax) {
			r.passed = false;
			r.reason = "ocekivano najmanje " + exp.minSyntax + " sintaksnih gresaka, a ima " + syntax;
			return r;
		}
		if (recovery < exp.minRecovery) {
			r.passed = false;
			r.reason = "ocekivano najmanje " + exp.minRecovery + " oporavaka, a ima " + recovery;
			return r;
		}

		for (String needle : exp.mustContain) {
			if (!err.contains(needle)) {
				r.passed = false;
				r.reason = "na stderr nedostaje ocekivana poruka: \"" + needle + "\"";
				return r;
			}
		}

		r.passed = true;
		return r;
	}

	/**
	 * Pokrece Compiler u zasebnom procesu, na istom JDK-u i sa istim classpath-om
	 * kao i sam TestRunner. Zahvaljujuci tome nije potrebno nikakvo dodatno
	 * podesavanje pri pokretanju iz Eclipse-a.
	 */
	private static int runCompiler(File mj, File obj, File out, File err) throws Exception {

		String javaBin = System.getProperty("java.home")
				+ File.separator + "bin" + File.separator + "java";
		String classpath = System.getProperty("java.class.path");

		ProcessBuilder pb = new ProcessBuilder(
				javaBin, "-cp", classpath,
				Compiler.class.getName(),
				mj.getPath(),
				obj.getPath());

		pb.redirectOutput(out);
		pb.redirectError(err);

		Process p = pb.start();

		if (!p.waitFor(TIMEOUT_SECONDS, TimeUnit.SECONDS)) {
			p.destroyForcibly();
			throw new IllegalStateException("Prevodilac se nije zavrsio u "
					+ TIMEOUT_SECONDS + " s za fajl " + mj.getName());
		}

		return p.exitValue();
	}

	private static String readFile(File f) {
		if (!f.isFile()) {
			return "";
		}
		try {
			return new String(Files.readAllBytes(f.toPath()), CS);
		} catch (IOException e) {
			return "";
		}
	}

	private static int count(String haystack, String needle) {
		int n = 0;
		int i = haystack.indexOf(needle);
		while (i >= 0) {
			n++;
			i = haystack.indexOf(needle, i + needle.length());
		}
		return n;
	}

	private static class Result {
		boolean passed;
		String summary = "";
		String reason = "";
	}

	/** Ocekivani ishod jednog testa, procitan iz komentara u .mj fajlu. */
	private static class Expectation {

		boolean ok;
		int minLexical;
		int minSyntax;
		int minRecovery;
		List<String> mustContain = new ArrayList<String>();

		static Expectation parse(File mj) throws IOException {

			Expectation e = new Expectation();
			boolean seen = false;

			for (String line : Files.readAllLines(mj.toPath(), CS)) {

				String t = line.trim();

				if (t.startsWith("// EXPECT-ERR:")) {
					String s = t.substring("// EXPECT-ERR:".length()).trim();
					if (!s.isEmpty()) {
						e.mustContain.add(s);
					}
					continue;
				}

				if (!t.startsWith("// EXPECT:")) {
					continue;
				}

				seen = true;
				String body = t.substring("// EXPECT:".length()).trim();

				for (String token : body.split("\\s+")) {
					if (token.isEmpty()) {
						continue;
					}
					if (token.equalsIgnoreCase("OK")) {
						e.ok = true;
						continue;
					}
					int sep = token.indexOf(">=");
					if (sep < 0) {
						continue;
					}
					String key = token.substring(0, sep).trim().toUpperCase();
					int value;
					try {
						value = Integer.parseInt(token.substring(sep + 2).trim());
					} catch (NumberFormatException nfe) {
						continue;
					}
					if ("LEXICAL".equals(key)) {
						e.minLexical = value;
					} else if ("SYNTAX".equals(key)) {
						e.minSyntax = value;
					} else if ("RECOVERY".equals(key)) {
						e.minRecovery = value;
					}
				}
			}

			return seen ? e : null;
		}
	}
}
