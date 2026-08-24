# MJCompiler — MicroJava Compiler (PP1)

Compiler for the **MicroJava** language. University project for *Programski prevodioci 1* (ETF
Beograd, school year 2025/2026). Builds a compiler that translates syntactically and semantically
valid MicroJava programs into MicroJava bytecode (`.obj`) that runs on the MicroJava VM (MJVM).

Defended project is a prerequisite for the exam. Min 20 points (full Level A) to pass. Target for
this project is **Level B (30 pts)**.

## Spec sources (read these, don't guess)

**Authoritative folder: `~/Desktop/PP1/`.** Do NOT use `~/Desktop/Programski Prevodioci/` — that's
a stale jan-feb term copy and no longer applies.

- `~/Desktop/PP1/PostavkaProjekta.pdf` — base project assignment (levels, grading, error-recovery
  points, deliverables, sample I/O). Its own title page says "Januarsko-februarski"/date 31.12.2025;
  ignore that label — treat it only as the baseline document that the amendments below modify.
- `~/Desktop/PP1/IzmeneIDopuneNaPostavkuProjekta.pdf` — **amendments that change the language
  requirements**, layered on top of the base postavka above (see "Amendments" section below). Its
  title page says "Julski ispitni rok"; per the user this project is being done in the August term
  as a continuation of Jul, and these amendments are the Jul→Avgust delta — apply them.
- `~/Desktop/PP1/SpecifikacijaJezikaMikroJava.pdf` — `[MJ]` language spec: grammar (A.2), semantics
  (A.3), context conditions (A.4), MJVM + instruction set (App. B). Note exact filename casing
  (`MikroJava`, capital J) — differs from the stale folder's `Mikrojava`.
- `~/Desktop/PP1/LinkoviDoSnimaka.rtf` — links to recorded lectures (intro/setup, lexical, syntax,
  semantic, codegen) of someone else building a similar-but-not-identical MJ compiler, used by the
  user to learn the JFlex/AST-CUP workflow. **The user is typing directly into this repo while
  following the recordings**, then adapting each increment to this project's actual spec (below) —
  expect the working tree to temporarily contain generic/mini-domaći-style code that gets reshaped
  toward the real MJ grammar as we go.
- Online `[MJ]`: http://ir4pp1.etf.rs/Domaci/mikrojava_2025_2026_jan.pdf
- Project template `[PT]`: http://ir4pp1.etf.rs/Domaci/2017-2018/pp1lab.templateAST.zip

When a grammar production, type rule, context condition, or VM instruction is involved, verify it
against the PDFs (base postavka + amendments + `[MJ]` spec) — do not reconstruct from memory of
"typical MicroJava".

## Amendments (Jul→Avgust) — override the base spec

- **No enums.** Skip enum grammar, semantics, and codegen entirely — not required at any level,
  despite the base `[MJ]` spec and grading tables including them.
- **Level A adds `findAny`**:
  `Statement := Designator "=" Designator "." "findAny" "(" Expr ")" ";"`.
  RHS `Designator` must denote a 1-D array of a built-in type; LHS `Designator` must denote a `bool`
  variable. Iterates the array first→last; result is `true` iff some element equals the value of
  `Expr`, else `false`.
- **Level B/C: `switch`/`case` is removed entirely, replaced by `map`**:
  `Statement := Designator "=" Designator "." "map" "(" ident "=>" Expr ")" ";"`.
  `Designator` = 1-D array of built-in type (Level B) or any type (Level C). `ident` must be a
  local/global variable of the same type as the array's elements. Iterates first→last, binding
  `ident` to the current element each iteration; result is a new array (same length, must already be
  declared) built by evaluating `Expr` (which references `ident`) per element.

These are hand-added productions, not in the printed `[MJ]` grammar tables — don't try to derive
them from the base spec's `Statement` alternatives.

## Toolchain (mandatory — use only versions from the course site)

- **JDK 1.8** — required. Do not use a newer JDK.
- **JFlex** — lexer generator (`.flex` → Java).
- **AST-CUP** (`cup_v10k.jar`) — locally-extended CUP that auto-generates AST node classes from the
  grammar. Use this, not vanilla CUP.
- **Symbol table** (`symboltable-1-1.jar`): http://ir4pp1.etf.rs/Domaci/symboltable-1-1.jar
  Source ships with it for reference only. **Do not unpack/modify/recompile the jar.** Extend only
  by subclassing and overriding (it has defined extension points).
- **MJVM runtime** (`mj-runtime-1.1.jar`): http://ir4pp1.etf.rs/Domaci/mj-runtime-1.1.jar
  Provides `Code`, `disasm`, `Run` tools for code gen and execution.

The `[PT]` template (download + unpack into the project) provides the directory layout and the
Ant build (`build.xml`) that runs JFlex + AST-CUP and compiles. Confirm exact build/run commands
from the unpacked template rather than assuming.

## Required structure, class names, and packages (strict)

The grader expects these exactly. Do not rename.

| Artifact | Location / name |
|---|---|
| Lexer spec | `src/spec/mjlexer.flex` |
| AST-CUP grammar | `src/spec/mjparser.cup` |
| Generated CUP spec | `src/spec/mjparser_astbuild.cup` |
| Generated lexer class | package `rs.ac.bg.etf.pp1` under `src/` |
| Generated AST node classes | package `rs.ac.bg.etf.pp1.ast` under `src/` |
| Main driver | class `rs.ac.bg.etf.pp1.Compiler` (has `main`, and `tsdump()`) |
| Semantic analyzer | class `rs.ac.bg.etf.pp1.SemanticAnalyzer extends rs.ac.bg.etf.pp1.ast.VisitorAdapter` |
| Code generator | class `rs.ac.bg.etf.pp1.CodeGenerator extends rs.ac.bg.etf.pp1.ast.VisitorAdapter` |

`Compiler.main` pipeline:
1. arg1 = input `.MJ` source path; arg2 = output `.obj` path.
2. Lex + parse → on success print AST via `toString()` on the root.
3. Run `SemanticAnalyzer` over the tree (pass visitor to root).
4. Call `tsdump()` to print the symbol table.
5. Run `CodeGenerator` over the tree → write executable `.obj` for MJVM.

## Hard rules (these lose points if broken)

- **No `precedence` in the `.cup`** except the single allowed `precedence left ELSE;` to resolve the
  dangling-else conflict.
- **No actions in the AST-CUP grammar `{: :}`** except syntax-error reporting/recovery. No semantic
  or code-gen logic in the parser, and no parser fields/methods that influence semantics or code gen
  (directly or indirectly).
- Nonterminals named per `[MJ]`; every production gets a **unique name** (AST-CUP derives a Java
  class per production from it). Add own nonterminals only when needed.
- Grammar must be **LALR(1)**.
- Don't modify the symbol-table jar; extend by subclassing only.
- Symbol-detection and error messages must follow the exact formats in the assignment (see Prilog 2
  sample output: line number, symbol name, symbol-table node dump).

## Phases & what each must do

1. **Lexical** — `.flex`; standard CUP interface (`next_token()`). Skip whitespace (`\t \r \n ' ' \b
   \f`) and `//` line comments. Tokens: idents, constants, keywords, operators. On lexical error:
   print unrecognized text + line + column, then continue.
2. **Syntax** — LALR(1) AST-CUP grammar. On error: log message (line + unambiguous description),
   recover, continue. On success: print AST. See per-level error-recovery points below.
3. **Semantic** — visitor over AST; update symbol table; check context conditions from `[MJ §A.4]`
   for the chosen level. Report detected symbols and semantic errors in required format.
4. **Code gen** — visitor over AST; emit valid MJVM bytecode using `Code`/`mj-runtime`; output `.obj`.

## Levels (cumulative; targeting Level B)

- **Level A (20 pts)** — basic statements, arithmetic expressions, arrays of primitive types,
  `length`, `read`/`print`, ternary with `CondFact` condition only (no `&&`/`||`), plus `findAny`
  (see Amendments). Program must have `main`, globals/locals (simple, array), global consts. No
  enums (amended out).
- **Level B (30 pts) — target** — all of A + control flow (`if/else`, `for`, `break`, `continue`,
  `return`), conditions with `&&`/`||`/relops, global function calls, plus `map` in place of
  `switch` (see Amendments).
- **Level C (40 pts)** — all of B + inner classes, inheritance, substitution, polymorphism, virtual
  method tables, object/array-of-object creation, abstract classes & abstract methods. `map` applies
  to arrays of any type at this level (not just built-in types).

Grading is all-or-nothing per level: you're scored at level X only if **all** of X's requirements
pass. Failing to implement all of Level A → project not considered. At defense there's an on-site
modification task per level (tests knowledge of JFlex/AST-CUP). Partial scores possible (e.g. C-level
project + B-level modification = 35).

### Error-recovery points (cumulative by level)
- **A**: global var definition (skip to `;` or next `,`); assignment statement (skip to `;`).
- **B**: formal parameter decl (skip to `,` or `)`); logical expr inside `if` (skip to `)`).
- **C**: inner-class field decl (skip to `;` or `{`); superclass-extends decl (skip to `{`).

### Symbol-use detection (cumulative)
- **A**: symbolic constants, global vars, local vars.
- **B**: global function calls, array element access, formal-arg use.
- **C**: inner-class object creation, field access, method calls.

## Language quick facts (full detail in `[MJ]`; amendments above take priority)

- Keywords: `program break class abstract else const if new print read return void extends
  continue for length`. **Not needed**: `enum`, `switch`, `case` (amended out). Contextual
  keyword-like literals from the amendments: `findAny`, `map` (used as literal tokens in their
  productions, not general reserved words — check how the base `[MJ]` lexer spec treats similar
  cases, e.g. `length`, before deciding whether to make them full keywords or handle them
  contextually in the grammar).
- Types: `int`, `char` (ASCII), `bool`; 1-D arrays; inner classes (abstract & concrete, Level C).
  Arrays and classes are reference types. No enum type.
- `ident = letter {letter|digit|"_"}`, `numConst = digit{digit}`, `charConst = '...'`,
  `boolConst = true|false`. Comments: `//` to end of line.
- Predeclared: `int char bool length null eol chr ord` (and `len`). `main` must be `void` with no
  params.
- Assignment-compatibility includes base-class ref ← derived-class ref (substitution, Level C).
- Impl limits: ≤256 locals, ≤65536 globals, ≤65536 fields/class, source ≤8 KB.

## MJVM (code-gen target)

Word = 32 bits. Memory areas: Code, StaticData (globals), Heap (objects/arrays; array starts with a
hidden length word; char arrays are byte arrays), ProcStack (activation records), ExprStack
(operands, empty between instructions). Object file = `"MJ"` + code size + global-word count +
mainPC + code bytes. Key instrs: `load/store(_n)`, `getstatic/putstatic`, `getfield/putfield`,
`const_n/const`, arithmetic, `new`/`newarray`, `aload/astore`/`baload/bastore`, `arraylength`,
`jmp`/`j<cond>`, `call`/`return`/`enter`/`exit`, `read`/`print`/`bread`/`bprint`, `trap`,
`invokevirtual` (virtual dispatch via VFT), `dup`/`dup_x1`/`dup_x2`. Full table in `[MJ]` App. B.2.

## Testing & deliverables

- `test/` folder: input `.MJ` files + matching `.out` (stdout) and `.err` (stderr) per test.
  Redirect: `>out.out 2>out.err`. Cover all grammar productions (valid) and all semantic-error
  combinations (invalid); test error recovery with syntactically broken programs.
- **Error reports → stderr; everything else → stdout.**
- `MJProjekat.docx` in project root: title page, short problem description, build/run/test commands,
  short description of test cases, short description of newly added classes.
- Submission must include `.flex`/`.cup`, all generated + hand-written `.java`, compiled `.class`,
  and the AST-CUP + Flex jars. Defended on lab machines, not your own — keep it portable.

## Working notes

- Workflow: user watches recorded lectures (see `LinkoviDoSnimaka.rtf` above) and types directly
  into this repo along with them, then pings for help adapting each increment to this project's
  actual spec/amendments. Don't get ahead of what's been asked — this is intentionally incremental
  so the user can reproduce the mechanics solo at the on-site defense modification task (no AI
  tools allowed there).
- `lib/` currently has `JFlex.jar` and `cup_v10k.jar` (correct locally-extended CUP). Still need to
  add `symboltable-1-1.jar` and `mj-runtime-1.1.jar` before semantic-analysis/codegen work starts.
- Comms: signal over prose; lead with the answer; flag assumptions before acting (see global
  AGENTS.md).
- Build is generation-heavy: regenerate lexer/parser after editing `.flex`/`.cup` before compiling.
  Stale generated classes are a common source of confusing errors.
