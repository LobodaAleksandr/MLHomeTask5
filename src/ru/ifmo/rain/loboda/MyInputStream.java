package ru.ifmo.rain.loboda;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class MyInputStream extends InputStream{
    private InputStream stream;
    private List<Character> buffer;
    private int pointer;
    private Stack<Integer> stack = new Stack<Integer>();

    MyInputStream(InputStream inputStream){
        stream = inputStream;
        buffer = null;
    }

    @Override
    public int read() throws IOException {
        int result;
        if(buffer != null){
            result = buffer.get(pointer++);
            if(pointer == buffer.size()){
                buffer = null;
            }
            return result;
        }
        result = stream.read();
        if(result != '('){
            return result;
        }
        stack.clear();
        stack.push(0);
        buffer = new ArrayList<Character>();
        pointer = 0;
        buffer.add('(');
        while(!stack.empty()){
            result = stream.read();
            if(result == '('){
                stack.push(buffer.size());
                buffer.add('(');
            } else if(result == ')'){
                buffer.add(']');
                buffer.set(stack.pop(), '[');
            } else {
                buffer.add((char)result);
            }
        }
        return read();
    }
}
