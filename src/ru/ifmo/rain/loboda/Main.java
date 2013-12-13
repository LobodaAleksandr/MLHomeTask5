package ru.ifmo.rain.loboda;

import java.io.*;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class Main {

    public static void main(String[] args) throws IOException {
        if(args.length != 2){
            System.err.println("Программа принимает ровно 2 аргумента");
            System.exit(1);
        }
        Parser parser = new Parser(new FileInputStream(args[0]));
        PrintWriter printWriter = new PrintWriter(new FileOutputStream(new File(args[1])));
        Checker checker = new Checker(new LinkedList<Expression>());
        int line = 0;
        boolean bad = false;
        while(parser.hasNext()){
            ++line;
            Expression e = parser.next();
            if(checker.check(e) == Checker.Type.ERROR){
                bad = true;
                printWriter.print("Вывод некорректен начиная с формулы номер " );
                printWriter.print(line);
                String error = checker.getLastError();
                if(error != null){
                    printWriter.print(": " + error);
                    break;
                }
            }
        }
        if(!bad){
            printWriter.print("Доказательство корректно.");
        }
        printWriter.close();
    }
}
