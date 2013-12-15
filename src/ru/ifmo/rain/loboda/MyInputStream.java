package ru.ifmo.rain.loboda;

import java.io.IOException;
import java.io.InputStream;
import java.io.PushbackInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class MyInputStream extends InputStream{
    private PushbackInputStream stream;
    private List<Character> buffer;
    private int pointer;
    private Stack<Integer> stack = new Stack<Integer>();

    MyInputStream(InputStream inputStream){
        stream = new PushbackInputStream(inputStream);
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
                while(true){
                    result = stream.read();
                    if(result != ' '){
                        break;
                    }
                }
                if(result == '\'' || result == '*' || result == '+' || result == '='){
                    buffer.add(']');
                    buffer.set(stack.pop(), '[');
                } else {
                    stack.pop();
                    buffer.add(')');
                }
                if(result != -1){
                    stream.unread(result);
                }
            } else {
                buffer.add((char)result);
            }
        }
        return read();
    }
}
