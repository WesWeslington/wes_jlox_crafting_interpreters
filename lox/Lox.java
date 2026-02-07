package lox;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

public class Lox {
    private static final Interpreter interpreter = new Interpreter();
    static boolean hadError = false;
    static boolean hadRuntimeError = false;
    public static boolean resolverTestMode = false;

    // Flags
    static boolean allowUninitializedVarDef = true; // makes it so 'var Foo;' is illegal and a runtime error

    public static void main(String[] args) throws IOException {
        ParseAllCommands(args);

    }

    private static void ParseAllCommands(String[] args) throws IOException
    {
        // Custom command line
        List<String> Commands = Arrays.asList
        (
            "RunAllTestsPath", "REPL", "RunFile"
        );

        for(String Command : Commands)
        {
            for(String arg : args)
            {
                if(arg.contains(Command))
                {
                    String ArgValue = arg.split(Command +"=")[1];
                    RunArgsInit(Command, ArgValue);
                }
            }
        }
    }

    private static void RunArgsInit(String Command, String ArgValue) throws IOException
    {

        switch(Command)
        {
            case "RunAllTestsPath":
                RunAllTests(ArgValue);
            break;
            case "RunTest":
                runFile(ArgValue);
            break;
            case "REPL":
                runPrompt();
            break;
            default:
            break;
        }
    }

    private static void RunAllTests(String PathToTestsDir) throws IOException
    {
        System.out.println("Running all tests using root folder: " + PathToTestsDir);

        File TestsRootFolder = new File(PathToTestsDir);
        File[] TestFiles = TestsRootFolder.listFiles();

        for(File TestFile : TestFiles)
        {
            if(TestFile.isFile() && TestFile.getAbsolutePath().endsWith(".jlox"))
            {
                runFile(TestFile.getAbsolutePath());
            }
        }
    }

    private static void runFile(String path) throws IOException 
    {
        byte[] bytes = Files.readAllBytes(Paths.get(path));
        run(new String(bytes, Charset.defaultCharset()));
        if(hadError) System.exit(65);
        if(hadRuntimeError) System.exit(70);
    }

    private static void runPrompt() throws IOException
    {
        InputStreamReader input = new InputStreamReader(System.in);
        BufferedReader reader = new BufferedReader(input);
        
        for(;;)
        {
            System.out.print("> ");
            String line = reader.readLine();

            // Handle single expression evaluation by turning it into an expression ONLY for REPL
            if(!line.endsWith(";"))
            {
                line = "print " + line + ";";
            }

            if(line == null) break;
            run(line);
            hadError = false;
        }
    }

    private static void run(String source){
        Scanner scanner = new Scanner(source);
        List<Token> tokens = scanner.scanTokens();

        // System.out.println(" ==== Entering the parser ==== ");
        // for(Token token : tokens)
        // {
        //     System.out.println(token.type);
        // }

        Parser parser = new Parser(tokens);
        List<Stmt> statements = parser.parse();

        Resolver resolver = new Resolver(interpreter);
        resolver.resolve(statements);

        if(hadError) return;

        // Stop if there was a syntax error
        if(hadError) { return; }

        // interpreter.interpret(expression);
        // System.out.println(new AstPrinter().print(expression)); // Prints abstract syntax tree

        interpreter.interpret(statements);
        // System.out.println(new AstPrinter().print(expression)); // Prints abstract syntax tree
    }

    static void error(int line, String message)
    {
        report(line, "", message);
    }

    static void runtimeError(RuntimeError error)
    {
        System.err.println(error.getMessage() + "\n[line ]" + error.token.line + "]");
        hadRuntimeError = true;
    }

    private static void report(int line, String where, String message)
    {
        System.err.println("[line " + line + "] Error" + where + ": " + message);
        hadError = true;
    }

    static void error(Token token, String message)
    {
        if(token.type == TokenType.EOF)
        {
            report(token.line, " at end", message);
        }
        else
        {
            report(token.line, " at '" + token.lexeme + "'", message);
        }
    }
}
