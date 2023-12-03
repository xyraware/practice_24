import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Scanner;

public class Test {
    public static void main(String[] args) throws IOException {
        //запись из файла в дерево
        Tree23<Integer> tree = new Tree23<Integer>();
        ArrayList<String> test=readFileAsString("PROCS.txt");//прочитали файл
        ArrayList<String> keys=getKeys(test);//деление информации
        ArrayList<String> data=getOtherData(test);
        for(int i=0;i<keys.size();i++) {
            tree.add(Integer.valueOf(keys.get(i)));
        }
        tree.inOrder();
        //вызов меню и выполнения функций, который просит пользователь
        menu(tree,test);
    }
    private static ArrayList<String> readFileAsString(String filePath) {
        Path path = Paths.get(filePath);
        String contents = null;
        try {
            contents = Files.readString(path, StandardCharsets.ISO_8859_1);
        } catch (IOException ex) {}
        String[] lines = contents.split("\n");
        ArrayList<String> words = new ArrayList<>();
        for(int i=0;i<lines.length;i++) {
            String [] Buf = lines[i].split(",");
            for(int j=0;j<Buf.length;j++) {
                words.add(Buf[j]);
            }
        }
        return words;
    }
    public static ArrayList<String> getKeys(ArrayList<String> test){
        ArrayList<String> keys=new ArrayList<>();
        keys.add(test.get(0));
        for(int i=0;i<test.size();i++){
            if((i%7==0)&&(i!=0)) {
                keys.add(test.get(i));
            }
        }
        return keys;
    }
    public static ArrayList<String> getOtherData(ArrayList<String> test){
        ArrayList<String> other_data=new ArrayList<>();
        for(int i=0;i<test.size();i++){
            if((i!=0)&&(i%7!=0)){
                other_data.add(test.get(i));
            }
        }
        return other_data;
    }
    public static void menu(Tree23<Integer> tree, ArrayList<String> data) throws IOException {
        while(true){
            Scanner console=new Scanner(System.in);
            System.out.println("Доброго времени суток! Введите букву, соответствующую функции:" +
                    "\nL – вывести на экран вершины дерева" +
                    "\nD n – (где n – ключ записи) удалить из дерева запись с ключом n" +
                    "\nA n – (где n – ключ записи) добавить в дерево запись с ключом n" +
                    "\nS – записать в файл PROCS.TXT все записи из дерева" +
                    "\nE- выход из программы");
            while((!console.hasNextLine())){
                System.out.println("Попробуйте снова!");
                console.next();
            }
            String choose=console.nextLine();
            if(choose.equals("L")){
                System.out.println("Вывести 23дерево:\n");
                tree.inOrder();
            }
            if(choose.equals("D")){
                int numberKeys=getKeyFromUser();
                System.out.println("Значение"+numberKeys+ " удалено");
                int count=0;
                ArrayList<String> key=getKeys(data);
                for(int i=0;i<data.size();i++) {
                    if (data.get(i).equals(numberKeys)) {
                        count = i;
                    }
                }
                tree.remove(numberKeys);
                data.remove(count);
                data.remove(count);
                data.remove(count);
                data.remove(count);
                data.remove(count);
                data.remove(count);
                data.remove(count);
            }
            if(choose.equals("A")){
                int numberKeys=getKeyFromUser();
                System.out.println("Значение"+numberKeys+ " добавлено");
                System.out.println("Запишите название процессора: ");
                String name_proc=console.nextLine();
                System.out.println("Запишите тактовую частоту: ");
                String tactovau_chastota=console.nextLine();
                System.out.println("Запишите размер кеш-памяти: ");
                String size_cache_memory=console.nextLine();
                System.out.println("Запишите частота ситсемной шины: ");
                String chastota_sustemnoi_shina=console.nextLine();
                System.out.println("Запишите результаты теста SPECint: ");
                String SPECint=console.nextLine();
                System.out.println("Запишите результаты теста SPECfp: ");
                String SPECfp=console.nextLine();
                tree.add(numberKeys);
                data.add(String.valueOf(numberKeys));
                data.add(name_proc);
                data.add(tactovau_chastota);
                data.add(size_cache_memory);
                data.add(chastota_sustemnoi_shina);
                data.add(SPECint);
                data.add(SPECfp);
            }
            if(choose.equals("E")){
                System.out.println("Конец программы. Удачного дня!");
                break;
            }
            if(choose.equals("S")){
                String line="";
                line=data.get(0)+", "+data.get(1)+",  "+data.get(2)+", "+data.get(3)+", "+data.get(4)+", "+
                        data.get(5)+", "+data.get(6)+"\n";
                for(int i=0;i<data.size();i++) {
                    if((i!=0)&&(i!=1)&&(i!=2)&&(i!=3)&&(i!=4)&&(i!=5) &&(i!=6)){
                        if ((i-6) % 7 != 0) {
                            line = line + data.get(i) + ", ";
                        }
                        if ((i-6) % 7 == 0) {
                            line = line + data.get(i) + "\n";
                        }
                    }
                }
                FileWriter file=new FileWriter("PROCS.txt",false);
                file.write(line);
                file.close();
            }
        }
    }
    public static int getKeyFromUser(){
        Scanner console=new Scanner(System.in);
        System.out.println("Введите ключ");
        while(!console.hasNextInt()){
            System.out.println("Это не целочисленное значение");
        }
        int numberKeys=console.nextInt();
        return numberKeys;
    }
}
