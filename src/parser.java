import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.time.Year;
import java.time.YearMonth;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashSet;
import java.util.Scanner;

public class parser {
    private static PrintWriter exportPerm;
    public static void main(String[] args) throws FileNotFoundException {
        Scanner userInput = new Scanner(System.in);
        System.out.println("Enter the month and the year that you want the schedule of (i.e. July 2023 -> 7,2023)");
        String input = userInput.nextLine();
        while (! input.contains(",")) {
            System.out.println("Invalid format. Enter the month and the year that you want the schedule of (i.e. July 2023 -> 7,2023)");
            input = userInput.nextLine();   
        }

        int year = Integer.parseInt(input.substring(input.indexOf(",")+1));
        int month = Integer.parseInt(input.substring(0,input.indexOf(",")));
        Calendar c = Calendar.getInstance();
        c.set(year, month-1, 1);
        int dayOfWeek = c.get(Calendar.DAY_OF_WEEK);
        // System.out.println(dayOfWeek);

        int firstSat = (7-dayOfWeek)+1;
        int firstSun = firstSat + 1;

        YearMonth cal = YearMonth.of(year, month);
        int daysInMonth = cal.lengthOfMonth();  

        // Export
        PrintWriter exportFile = new PrintWriter("./export/"+(month < 10 ? "0"+month : month)+"_"+year+"_schedule.csv");

        // Create Patterns
        System.out.println("What is the size of each group?");
        int size = userInput.nextInt();

        try {
            Scanner size_perm = new Scanner(new File(size+"_permutations.txt"));
            size_perm.close();
        }
        catch (Exception e) {
            String perm = "";
            for (int i = 0; i < size; i++) {
                perm += i;
            }
            exportPerm = new PrintWriter(size+"_permutations.txt");
            exportPermutations(perm, "");
            exportPerm.close();
        }
        
        System.out.println("How many groups are there?");
        int group_count = userInput.nextInt();

        // Import Group + Member names
        String[] group_names = new String[group_count];
        String[] member_names = new String[group_count*size];
        Scanner list_import = new Scanner(new File("./list.txt"),"UTF-8");
        for (int i = 0; i < group_count; i++) {
            group_names[i] = list_import.nextLine();
            for (int j = 0; j < size; j++) {
                member_names[i*5+j] = list_import.nextLine();
            }
        }
        // System.out.println(Arrays.toString(member_names));
        // System.out.println(Arrays.toString(group_names));

        Scanner size_perm = new Scanner(new File(size+"_permutations.txt"));
        HashSet<String> perms = new HashSet<>();
        while (size_perm.hasNextLine()) {
            perms.add(size_perm.nextLine());
        }
        size_perm.close();

        String firstLine = "Day,";
        String secondLine = "Day of Week,";
        String thirdLine = "Time,";
        int satCount = 0;
        int sunCount = 0;
        while (true) {
            if (firstSat <= daysInMonth) {
                firstLine += firstSat+",";
                firstLine += firstSat+",";
                firstSat += 7;
                secondLine += "Sat,Sat,";
                thirdLine += "5:00 PM,8:00 PM,";
                satCount++;
            }
            else {
                break;
            }
            if (firstSun <= daysInMonth) {
                firstLine += firstSun+",";
                firstLine += firstSun+",";
                firstLine += firstSun+",";
                secondLine += "Sun,Sun,Sun,";
                firstSun += 7;
                thirdLine += "8:00 AM,9:30 AM,12:30 PM,";
                sunCount++;
            }
            else {
                break;
            }
        }
        exportFile.println(firstLine);
        exportFile.println(secondLine);
        exportFile.println(thirdLine);

        // Pick random schedule from permutations
        String[] chosen_perms = new String[satCount];
        for (int i = 0; i < satCount; i++) {
            int random = (int) (Math.random()*perms.size());
            int j = 0;
            String perm = "";
            for (String string : perms) {
                if (j == random) {
                    perm = string;
                    perms.remove(string);
                    break;
                }
                j++;
            }
            chosen_perms[i] = perm;
        }


        int curSat;
        int curSun;
        for (int j = 0; j < group_count; j++) {
            curSat = 0;
            curSun = 0;
            String cur = group_names[j]+",";
            for (int l = 0; l < satCount; l++) {
                for (int k = 0; k < size; k++) {
                    if (k == 0) curSat++;
                    if (k == 2) curSun++;
                    if (curSat > satCount || curSun > sunCount) {
                        break;
                    }
                    int pos = chosen_perms[curSat-1].charAt(k)-'0';
                    cur += member_names[j*size+pos]+",";
                }
            }
            exportFile.println(cur);
        }

        exportFile.close();
    }

    public static void exportPermutations(String str, String ans) {
        if (str.length() == 0) {
            exportPerm.println(ans);
            return;
        }
 
        for (int i = 0; i < str.length(); i++) {
            char c = str.charAt(i);
            String rest = str.substring(0, i) + str.substring(i + 1);
            exportPermutations(rest, ans + c);
        }
    }
}