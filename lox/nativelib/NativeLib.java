package lox.nativelib;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lox.Interpreter;
import lox.Lox;
import lox.LoxCallable;
import lox.Resolver;

public class NativeLib 
{
    public static Map<String, LoxCallable> NativeFunctions()
    {
        Map<String, LoxCallable> NativeFunctionMapRet = new HashMap<>();

        NativeFunctionMapRet.put("clock", new LoxCallable()
        {
            @Override
            public int arity(){ return 0;}

            @Override
            public Object call(Interpreter interpreter, List<Object> arguments)
            {   
                // Function body
                return (double)System.currentTimeMillis() / 1000.0;
            }

            @Override
            public String toString() {return "<native clock>";}
        });

        NativeFunctionMapRet.put("sleep", new LoxCallable()
        {
            @Override
            public int arity(){ return 1;}

            @Override
            public Object call(Interpreter interpreter, List<Object> arguments)
            {   
                double endTime = 0.0;
                try 
                {
                    endTime = (double)System.currentTimeMillis() + (double)arguments.get(0) * 1000.0;
                } 
                catch (Exception e) 
                {
                    throw new RuntimeException("Issue with parameters in native sleep function: ", e);
                }

                while( (double)System.currentTimeMillis() < endTime)
                {

                }
                // Function body
                return null;
            }

            @Override
            public String toString() {return "<native sleep>";}
        });

        NativeFunctionMapRet.put("round", new LoxCallable()
        {

            @Override
            public int arity(){ return 1;} // TODO: make 2 arguments, the 2nd arg should be for decimal places (for now, rounding to nearest int is fine)

            @Override
            public Object call(Interpreter interpreter, List<Object> arguments)
            {   
                try 
                {
                    return Math.round((double)arguments.get(0));
                } 
                catch (Exception e) 
                {
                    throw new RuntimeException("Issue with parameters in native round function: ", e);
                }
            }

            @Override
            public String toString() {return "<native round>";}
        });
        
        NativeFunctionMapRet.putAll(TestLibrary());

        return NativeFunctionMapRet;
    }

    public static Map<String, LoxCallable> TestLibrary()
    {
        Map<String, LoxCallable> NativeTestFunctionMapRet = new HashMap<>();

        NativeTestFunctionMapRet.put("toggleResolverTests", new LoxCallable()
        {

            @Override
            public int arity(){ return 1;} 

            @Override
            public Object call(Interpreter interpreter, List<Object> arguments)
            {   
                try 
                {
                    Lox.resolverTestMode = ((boolean)arguments.get(0));
                    System.out.println("[Resolver Testmode: " + Lox.resolverTestMode +"]");
                    return null;
                } 
                catch (Exception e) 
                {
                    throw new RuntimeException("Issue with parameters in native round function: ", e);
                }
            }

            @Override
            public String toString() {return "<native toggleResolverTests>";}
        });

        NativeTestFunctionMapRet.put("getResolverErrorCount", new LoxCallable()
        {
            @Override
            public int arity(){ return 0;}

            @Override
            public Object call(Interpreter interpreter, List<Object> arguments)
            {   
                // Function body
                return (double)Resolver.testAssertCount;
            }

            @Override
            public String toString() {return "<native getResolverErrorCount>";}  
        });

        return NativeTestFunctionMapRet;
    }
}
   
