package week2;
import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.Stack;

public class Main {
    //Attributes
    ArrayList<bareVar> varArray = new ArrayList<>();
    ArrayList<String> codeArray = new ArrayList<>();
    Stack<Integer> branches = new Stack();
    Stack<bareVar> controllers = new Stack();
    Scanner scanner;
    int codeIndex = -1;
    int loops = 0;

    //Components
    JFrame f;
    //JLABELS USE HTML USE <BR> INSTEAD OF /n
    private JLabel info;
    private JScrollPane varTracker = new JScrollPane();
    private JPanel p;
    private final JButton chooseFile = new JButton("Choose BareBones Text File");
    private final JButton step = new JButton("Step ->");


    //Main
    public static void main(String[] args) {
        Main demo = new Main();
        demo.configureGUI();
        demo.eventSetup();
        demo.f.setVisible(true);
    }

    public void configureGUI() {
        //defines the graphical layout of the program
        //frame
        f = new JFrame("BareBones Interpreter- Luke Brown");
        f.setSize(600, 300);
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        //button
        chooseFile.setSize(200, 100);
        step.setSize(200, 100);

        //JLabels
        info = new JLabel("Select a file to begin");
        info.setSize(200, 300);

        //layout
        f.setLayout(new BorderLayout());
        f.add(chooseFile, BorderLayout.NORTH);
        p = new JPanel();
        p.setLayout(new GridLayout(1, 2));
        p.add(info);
        p.add(varTracker);
        f.add(p, BorderLayout.CENTER);
        f.add(step, BorderLayout.SOUTH);
    }

    public void eventSetup(){
        //defines the mouse and button events
        step.setEnabled(false);


        chooseFile.addActionListener(input -> {
            //asking for code file using popup
            final JFileChooser fc = new JFileChooser();
            fc.showOpenDialog(f);
            File codeIn = fc.getSelectedFile();
            try {
                scanner = new Scanner(codeIn);
            } catch (FileNotFoundException e) {
                throw new RuntimeException(e);
            }
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                codeArray.add(line.substring(0, line.length() - 1));
            }
            //info.setText(codeArray.toString());
            scanner.close();
            chooseFile.setEnabled(false);
            step.setEnabled(true);
            info.setText("Press step to run the first line");
        });

        step.addActionListener(run -> {
            //gets the next line of code and runs it
            codeIndex++;
            if (codeIndex == codeArray.size() - 1 && loops == 0) {
                step.setEnabled(false);
            }
            try{
                execute(codeArray.get(codeIndex));
            } catch (IndexOutOfBoundsException e) {
                step.setEnabled(false);
            }

        });
    }

    public void execute(String code) {
        //switch case is a bit janky here due to whitespace
        //using simple ifs instead
        bareVar variable = null;
        if(!code.contains("while") && !code.contains("end")){
            variable = findVariable(code);
        }

        bareVar counter = null;

        if (code.contains("clear")) {
            variable.zero();
        }
        if (code.contains("incr")) {
            variable.incr();
        }
        if (code.contains("decr")) {
            variable.decr();
        }
        if(code.contains("end")){
            if (loops > 0){
                end();
            }
        }
        if (!code.contains("while")) {
            updateGUI();
        } else {
            loops++;
            counter = findVariable(code.substring(code.lastIndexOf("while ")+1,code.indexOf(" not 0")));

            /*
            The idea is that when a while is encountered the start of the loops is pushed
            as well as the varibale controlling that loop
            when an end is encountered, it checks the condition, and sets the codeIndex back if
            needed
             */
            branches.push(codeIndex);
            controllers.push(counter);
            info.setText("<html> <body> <p2> Most recent code execute: </p2> <br> <p2> line " + (codeIndex + 1) + " <br> <p2>" + codeArray.get(codeIndex) + "</p2> <br> <p2> While Loop "+loops+" started </p2> </body> </html>");
        }
    }

    public bareVar findVariable(String code2) {
        //finds the variable name, checks the array and creates it if it does not exist already
        String varName = code2.substring(code2.lastIndexOf(" ") + 1);
        for (bareVar element : varArray) {
            if (varName.equals(element.getName())) {
                return element;
            }
        }
        bareVar newVariable = new bareVar(varName);
        varArray.add(newVariable);
        return newVariable;
    }

    public void updateGUI() {
        info.setText("<html> <body> <p2> Most recent code execute: </p2> <br> <p2> line " + (codeIndex + 1) + " <br> <p2>" + codeArray.get(codeIndex) + "</p2> </body> </html>");
        String[][] data = new String[varArray.size()][2];
        for (int i = 0; i < varArray.size(); i++) {
            bareVar element2 = varArray.get(i);
            data[i][0] = element2.getName();
            data[i][1] = Integer.toString(element2.getValue());
        }
        //Makes table
        String[] columns = {"Variable", "Value"};
        JTable jt = new JTable(data, columns);
        jt.setFillsViewportHeight(true);
        p.setVisible(false);
        p.remove(1);
        varTracker = new JScrollPane(jt);
        p.add(varTracker);
        p.setVisible(true);
    }

    public void end(){
        bareVar x = controllers.peek();
        if (x.getValue()>0){
            codeIndex = branches.peek();
            info.setText("<html> <body> <p2> Most recent code execute: </p2> <br> <p2> line " + (codeIndex + 1) + " <br> <p2>" + codeArray.get(codeIndex) + "</p2> <br> <p2>"+x.getName()+"not 0, looping </p2> </body> </html>");
        }
        else{
            controllers.pop();
            branches.pop();
            loops--;

        }

    }
}


