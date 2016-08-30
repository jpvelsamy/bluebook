package hackerrank;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Scanner;

public class DateFormatSolution {

	public static void main(String[] args) {
		Scanner in = new Scanner(System.in);
        String time = in.nextLine();
        time = time.replace(" ", "");
        DateFormat readFormat = new SimpleDateFormat( "hh:mm:ssa");
        DateFormat writeFormat = new SimpleDateFormat( "HH:mm:ss");
        try {
			Date input= readFormat.parse(time);
			String output = writeFormat.format(input);
			System.out.println(output.split(" ")[0]);
		} catch (ParseException e) 
        {
			System.err.println("You gave a misleading time="+time);
			System.err.println("Enter only in this format, for e.g 07:05:45PM");
			System.err.println("Make sure you dont have other non standard input");
			e.printStackTrace();
		}
        in.close();
        
	}
}
