
package rs.ac.bg.etf.pp1;

import java_cup_runtime.Symbol;

%%

%{

	// ukljucivanje informacije o poziciji tokena
	private Symbol new_symbol(int type) {
		return new Symbol(type, yyline+1, yycolumn);
	}
	
	// ukljucivanje informacije o poziciji tokena
	private Symbol new_symbol(int type, Object value) {
		return new Symbol(type, yyline+1, yycolumn, value);
	}

%}

%cup
%line
%column

%xstate COMMENT

%eofval{
	return new_symbol(sym.EOF);
%eofval}

%%

" "								{ }
"\b" 							{ }
"\t" 							{ }
"\r\n" 							{ }
"\f" 							{ }
"\r" 							{ }
"\n" 							{ }

"program" 						{ return new_symbol(sym.PROG, yytext()); }
"const"							{ return new_symbol(sym.CONST, yytext()); }
"new"							{ return new_symbol(sym.NEW, yytext()); }
"print"							{ return new_symbol(sym.PRINT, yytext()); }
"read"							{ return new_symbol(sym.READ, yytext()); }
"void"							{ return new_symbol(sym.VOID, yytext()); }
"return"						{ return new_symbol(sym.RETURN, yytext()); }
"if"							{ return new_symbol(sym.IF, yytext()); }
"else"							{ return new_symbol(sym.ELSE, yytext()); }
"break"							{ return new_symbol(sym.BREAK, yytext()); }
"continue"						{ return new_symbol(sym.CONTINUE, yytext()); }
"for"							{ return new_symbol(sym.FOR, yytext()); }
"length"						{ return new_symbol(sym.LENGTH, yytext()); }

/* Amandmani (Jul->Avgust): findAny za Nivo A, map za Nivo B/C — tretiramo ih kao ključne reči,
   isto kao sto [MJ] tretira "length" iako se pojavljuju samo iza tačke u Designator-u. */
"findAny"						{ return new_symbol(sym.FINDANY, yytext()); }
"map"							{ return new_symbol(sym.MAP, yytext()); }

"+" 							{ return new_symbol(sym.PLUS, yytext()); }
"-"								{ return new_symbol(sym.MINUS, yytext()); }
"*"								{ return new_symbol(sym.MUL, yytext()); }
"/"								{ return new_symbol(sym.DIV, yytext()); }
"%"								{ return new_symbol(sym.MOD, yytext()); }
"=="							{ return new_symbol(sym.EQ, yytext()); }
"!="							{ return new_symbol(sym.NEQ, yytext()); }
">"								{ return new_symbol(sym.GT, yytext()); }
">="							{ return new_symbol(sym.GEQ, yytext()); }
"<"								{ return new_symbol(sym.LT, yytext()); }
"<="							{ return new_symbol(sym.LEQ, yytext()); }
"&&"							{ return new_symbol(sym.AND, yytext()); }
"||"							{ return new_symbol(sym.OR, yytext()); }
"=>"							{ return new_symbol(sym.ARROW, yytext()); }
"="								{ return new_symbol(sym.ASSIGN, yytext()); }
"++" 							{ return new_symbol(sym.INC, yytext()); }
"--"							{ return new_symbol(sym.DEC, yytext()); }
";"								{ return new_symbol(sym.SEMI, yytext()); }
":"								{ return new_symbol(sym.COLON, yytext()); }
","								{ return new_symbol(sym.COMMA, yytext()); }
"."								{ return new_symbol(sym.DOT, yytext()); }
"("								{ return new_symbol(sym.LPAREN, yytext()); }
")"								{ return new_symbol(sym.RPAREN, yytext()); }
"["								{ return new_symbol(sym.LBRACK, yytext()); }
"]"								{ return new_symbol(sym.RBRACK, yytext()); }
"{"								{ return new_symbol(sym.LBRACE, yytext()); }
"}"								{ return new_symbol(sym.RBRACE, yytext()); }
"?"								{ return new_symbol(sym.QUESTION, yytext()); }


"//"							{ yybegin(COMMENT); }
<COMMENT> "\r\n"				{ yybegin(YYINITIAL); }
<COMMENT> "\r"					{ yybegin(YYINITIAL); }
<COMMENT> "\n"					{ yybegin(YYINITIAL); }
<COMMENT> .						{ yybegin(COMMENT); }

[0-9]+ 							{ return new_symbol(sym.NUMBER, new Integer (yytext())); }

"'"."'" 						{return new_symbol(sym.CHARACTER, new Character(yytext().charAt(1))); }
("true"|"false") 				{return new_symbol(sym.BOOL, yytext().equals("true") ? 1 : 0); }
([a-z]|[A-Z])[a-zA-Z0-9_]* 		{return new_symbol(sym.IDENT, yytext()); }

. 								{ System.err.println("Leksicka greska ("+yytext()+") na liniji "+(yyline+1) + " u koloni " + (yycolumn + 1) + "\n"); }

