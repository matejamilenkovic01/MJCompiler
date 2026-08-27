package rs.ac.bg.etf.pp1;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;

import org.apache.log4j.Logger;
import org.apache.log4j.xml.DOMConfigurator;

import java_cup.runtime.Symbol;
import rs.ac.bg.etf.pp1.ast.*;
import rs.ac.bg.etf.pp1.util.Log4JUtils;
import rs.etf.pp1.symboltable.*;
import rs.etf.pp1.symboltable.concepts.Obj;
import rs.etf.pp1.symboltable.concepts.Struct;

public class Compiler {

	/** Parsiranje uspesno, bez ijedne prijavljene greske. */
	public static final int EXIT_OK = 0;
	/** Greske su prijavljene, ali je parser izvrsio oporavak i stigao do kraja fajla. */
	public static final int EXIT_ERRORS = 1;
	/** Parsiranje prekinuto - oporavak nije bio moguc. */
	public static final int EXIT_FATAL = 2;
	/** Pogresan poziv ili ulazni fajl nije citljiv. */
	public static final int EXIT_USAGE = 3;

	/** Koristi se kada je program pozvan bez argumenata (zgodno pri pokretanju iz Eclipse-a). */
	private static final String DEFAULT_SOURCE = "test/program.mj";

	static {

		DOMConfigurator.configure(Log4JUtils.instance().findLoggerConfigFile());
		Log4JUtils.instance().prepareLogFile(Logger.getRootLogger());

	}

	public static void main(String[] args) {

		Logger log = Logger.getLogger(Compiler.class);

		// arg1 = putanja do ulaznog .mj fajla, arg2 = putanja do izlaznog .obj fajla.
		// Drugi argument se koristi tek u fazi generisanja koda.
		String sourcePath = args.length > 0 ? args[0] : DEFAULT_SOURCE;

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
			Tab.init();
			Struct boolType = new Struct(Struct.Bool);
			Obj boolObj = Tab.insert(Obj.Type, "bool", boolType);
			boolObj.setAdr(-1);
			boolObj.setLevel(-1);
			
			/* Semanticka analiza */
			SemAnalyzer sa = new SemAnalyzer();
			prog.traverseBottomUp(sa);
			
			/* Ispis tabele simbola */
			log.info("=================================================");
			Tab.dump();

			if (!p.errorDetected && sa.passed()) {
				log.info("Parsiranje uspesno zavrseno!");
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
