import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.net.*;
import java.io.*;

public class BusSchedule {
    private final static String requestProp = "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.11 (KHTML, like Gecko) Chrome/23.0.1271.95 Safari/537.11";
    private static Scanner sc;
    private static URLConnection ct;
    private static BufferedReader in;

    private static URLConnection busRouteURL;
    private static BufferedReader busRouteIn;

    private static void init() throws Exception {
        sc = new Scanner(System.in);
        ct = new URL("https://www.communitytransit.org/busservice/schedules/").openConnection();
        // ct.setRequestProperty("user-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.11 (KHTML, like Gecko) Chrome/23.0.1271.95 Safari/537.11");
        ct.setRequestProperty("user-Agent", requestProp);
        in = new BufferedReader(new InputStreamReader(ct.getInputStream()));
    }

    public static void main(String[] args) throws Exception {
        init();

        String inputLine = "";
        String text = "";
        while ((inputLine = in.readLine()) != null) {
            text += inputLine;
        }

        System.out.print("Please enter a letter that your destination starts with: ");
        String temp = sc.nextLine();
        temp = temp.toUpperCase();

        String searchStr = "<h3>(" + temp.charAt(0) + ".*?)</h3>(.*?)(<hr |Boeing route)";
        Pattern pattern = Pattern.compile(searchStr);
        Matcher matcher = pattern.matcher(text);

        while (matcher.find()) {
            String dest = matcher.group(1);
            dest = dest.replaceAll("&amp;", "&");
            System.out.println("Destination: " + dest);

            String routes = matcher.group(2);
            Pattern routePattern = Pattern.compile("<a href=.*?>(.*?)</a>");
            Matcher routeMatcher = routePattern.matcher(routes);
            // System.out.println(matcher.group(2));
            while (routeMatcher.find()) {
                System.out.println("Bus Number: " + routeMatcher.group(1));
            }
            System.out.println("+++++++++++++++++++++++++++++++++++");

        }

        System.out.print("Please enter a route ID as a string: ");
        String routeNumber = sc.nextLine();
        routeNumber = routeNumber.replaceAll("/", "-");

        String routeURL = "https://www.communitytransit.org/busservice/schedules/route/";
        routeURL += routeNumber;
        System.out.println("\nThe link for your route is: " + routeURL + "\n");

        busRouteURL = new URL(routeURL).openConnection();
        // busRouteURL.setRequestProperty("user-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.11 (KHTML, like Gecko) Chrome/23.0.1271.95 Safari/537.11");
        busRouteURL.setRequestProperty("user-Agent", requestProp);
        busRouteIn = new BufferedReader(new InputStreamReader(busRouteURL.getInputStream()));

        String routeHtml = "";
        while ((inputLine = busRouteIn.readLine()) != null) {
            routeHtml += inputLine;
        }

        String routeRegex = "<div class=\"table-responsive\">.*?<small>(.*?)</small>";
        routeRegex += ".*?<tr>(.*?)</tr>";
        Pattern routePattern = Pattern.compile(routeRegex);
        Matcher routeMatcher = routePattern.matcher(routeHtml);

        while (routeMatcher.find()) {
            System.out.println("Destination: " + routeMatcher.group(1));
            // System.out.println(routeMatcher.group(2));
            String stops = routeMatcher.group(2);

            String stopRegex = ".*?<strong class=\"fa fa-stack-1x\">(.*?)</strong>.*?";
            stopRegex += "<p>(.*?)</p>.*?";
            Pattern stopPattern = Pattern.compile(stopRegex);
            Matcher stopMatcher = stopPattern.matcher(stops);
            while (stopMatcher.find()) {
                System.out.print("Stop Number: ");
                System.out.println(stopMatcher.group(1) + " is " + stopMatcher.group(2));
            }

            System.out.println("+++++++++++++++++++++++++++++++++++");
        }


        sc.close();
    }
}