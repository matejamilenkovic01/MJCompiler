package rs.ac.bg.etf.pp1;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;

import org.apache.log4j.Logger;
import org.apache.log4j.xml.DOMConfigurator;

import java_cup.runtime.Symbol;
import rs.ac.bg.etf.pp1.ast.*;
import rs.ac.bg.etf.pp1.util.Log4JUtils;
import rs.etf.pp1.mj.runtime.Code;
import rs.etf.pp1.symboltable.*;

public class Compiler {

	/** Parsiranje uspesno, bez ijedne prijavljene greske. */
	public static final int EXIT_OK = 0;
	/** Greske su prijavljene, ali je parser izvrsio oporavak i stigao do kraja fajla. */
	public static final int EXIT_ERRORS = 1;
	/** Parsiranje prekinuto - oporavak nije bio moguc. */
	public static final int EXIT_FATAL = 2;
	/** Pogresan poziv ili ulazni fajl nije citljiv. */
	public static final int EXIT_USAGE = 3;

	/** Koriste se kada je program pozvan bez argumenata (zgodno pri pokretanju iz Eclipse-a). */
	private static final String DEFAULT_SOURCE = "test/program.mj";
	private static final String DEFAULT_TARGET = "test/program.obj";

	static {

		DOMConfigurator.configure(Log4JUtils.instance().findLoggerConfigFile());
		Log4JUtils.instance().prepareLogFile(Logger.getRootLogger());

	}

	/**
	 * Ispis sadrzaja tabele simbola. Koristi MyDumpSymbolTableVisitor kako bi se
	 * tip bool ispisao imenom, a ne prazno (vidi MyDumpSymbolTableVisitor).
	 */
	public static void tsdump() {
		Tab.dump(new MyDumpSymbolTableVisitor());
	}

	public static void main(String[] args) {

		Logger log = Logger.getLogger(Compiler.class);

		// arg1 = putanja do ulaznog .mj fajla, arg2 = putanja do izlaznog .obj fajla.
		String sourcePath = args.length > 0 ? args[0] : DEFAULT_SOURCE;
		String targetPath = args.length > 1 ? args[1] : DEFAULT_TARGET;

		if (args.length == 0) {
			log.info("Nije zadat ulazni fajl, koristi se podrazumevani: " + DEFAULT_SOURCE);
		}

		File sourceCode = new File(sourcePath);

		if (!sourceCode.isFile()) {
			log.error("Ulazni fajl ne postoji: " + sourceCode.getAbsolutePath());
			System.exit(EXIT_USAGE);
		}

		log.info("Compiling source file: " + sourceCode.getAbsolutePath());

		Reader br = null;

		try {

			br = new BufferedReader(new FileReader(sourceCode));

			Yylex lexer = new Yylex(br);

			/* Formiranje AST */
			MJParser p = new MJParser(lexer);
			Symbol s = p.parse(); // formiranje AST

			// Ako oporavak od greske ne uspe, parse() se prekida pre kraja fajla i na vrhu
			// steka ostaje bilo koji cvor (npr. DeclList_const) - dakle s.value tada NIJE
			// Program. Bez ove provere kast baca ClassCastException, ciji stack trace
			// zamagljuje pravu, vec prijavljenu sintaksnu gresku.
			if (s == null || !(s.value instanceof Program)) {
				log.error("Parsiranje prekinuto pre kraja fajla - sintaksno stablo nije formirano.");
				System.exit(EXIT_FATAL);
			}

			Program prog = (Program) s.value;

			// ispis AST
			log.info(prog.toString(""));
			log.info("=================================================");
			
			/* Inicijalizacija tabele simbola */
			MyTab.init();
			
			/* Semanticka analiza.

			   Pokrece se samo ako sintaksna analiza nije prijavila greske. Postavka to ne
			   trazi izricito, ali oporavak od greske ostavlja nepotpuna podstabla (npr.
			   IfCondition_err nema Condition, VarItem_err nema ime), pa bi semanticke
			   provere ili pucale na null ili prijavljivale lavinu lazenih gresaka.
			   Isti obrazac vazi i dalje u lancu: generisanje koda se po postavci pokrece
			   tek nad stablom koje je "zadovoljilo uslove semanticke provere". */
			SemanticAnalyzer sa = new SemanticAnalyzer();
			if (!p.errorDetected) {
				prog.traverseBottomUp(sa);
			}
			
			/* Ispis tabele simbola */
			log.info("=================================================");
			tsdump();

			if (!p.errorDetected && sa.passed()) {
				
				/* Generisanje koda */
				
				File objFile = new File(targetPath);
				if (objFile.exists()) {
					objFile.delete();
				}

				CodeGenerator cg = new CodeGenerator(sa.getProgramObj());
				prog.traverseBottomUp(cg);

				/* Broj reci koje MJVM rezervise u statickoj zoni = broj globalnih promenljivih.
				   Semanticka analiza ga je zapamtila pre zatvaranja opsega programa; posle
				   closeScope() taj podatak vise nije dostupan. */
				Code.dataSize = sa.nVars;
				Code.mainPc = cg.getMainPc();

				FileOutputStream objOut = new FileOutputStream(objFile);
				try {
					Code.write(objOut);
				} finally {
					objOut.close();
				}

				log.info("Generisanje uspesno zavrseno: " + objFile.getPath());
				System.exit(EXIT_OK);
			}

			// Greske su prijavljene, ali je parsiranje stiglo do kraja - oporavak je uspeo.
			log.error("Parsiranje NIJE uspesno zavrseno!");
			System.exit(EXIT_ERRORS);

		} catch (Exception e) {
			// Ovde se stize samo ako oporavak nije uspeo (unrecovered_syntax_error)
			// ili ako je doslo do neocekivane greske pri citanju fajla.
			log.error("Parsiranje prekinuto: " + e.getMessage(), e);
			System.exit(EXIT_FATAL);
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e1) {
					log.error(e1.getMessage(), e1);
				}
			}
		}

	}

}
