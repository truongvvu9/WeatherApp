import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Scanner;

public class WeatherApp {
    private static String apiKey = "";
    private static String city = "";
    private static String response = "";
    private static String previousCity = "";

    public static void main(String[] args) throws IOException {
        weatherMenu();
    }
    private static void weatherMenu() throws IOException {
        while(true){
            Scanner scanner = new Scanner(System.in);
            if(city.equals("")){
                System.out.println("Current city chosen: none");
            }else{
                System.out.println("Current city chosen: " + city);
                if(response.equals("")){
                    System.out.println("Weather information is not ready to be displayed for city of " + city +". Please choose choice #4 to get the weather information.");
                }else{
                    System.out.println("Weather information is ready to be displayed for city of " + city + ". You can now choose choice #5 to display the weather information.");
                }
            }
            if(previousCity.equals("")){
                System.out.println("Previous city: none");
            }else{
                System.out.println("Previous city: " + previousCity);
            }

            System.out.println("Choose one: ");
            System.out.println("1.Enter api key from weatherstack.com");
            System.out.println("2.Enter city");
            System.out.println("3.Load api key from apikey.txt");
            System.out.println("4.Get weather information");
            System.out.println("5.Display weather information");
            System.out.println("6.Exit");
            boolean hasInt = scanner.hasNextInt();
            if(hasInt){
                int input = scanner.nextInt();
                scanner.nextLine();
                if(input == 1){
                    getInputForApiKey();
                }else if(input == 2){
                    getInputForCity();
                }else if(input == 3){
                    loadAPIKEYFromTextFile();
                }else if(input == 4){
                    if(!apiKey.equals("") && !city.equals("")){
                        response = getWeatherInfornation();
                        System.out.println("Successfully received weather information");
                    }else{
                        System.out.println("You need to enter api key and city!");
                    }
                }else if(input == 5){
                    if(!response.equals("")){
                        displayWeatherInformation(response);
                    }else{
                        System.out.println("You have to choose choice #4 first to get the weather information before displaying.");
                    }
                }else if(input == 6){
                    break;
                } else{
                    System.out.println("Invalid choice. Please try again.");
                }
            }else{
                System.out.println("Please enter a valid choice.");
            }

        }
    }
    private static void getInputForApiKey() throws IOException {
        while(true){
            Scanner scanner = new Scanner(System.in);
            System.out.println("Enter api key from weatherstack.com: ");
            String input = scanner.nextLine();
            if(input.length() == 32){
                apiKey = input;
                break;
            }else{
                System.out.println("Api key needs to be 32 characters. Please try again.");
            }
        }
        while(true){
            Scanner scanner = new Scanner(System.in);
            System.out.println("Do you want to save this api key to a text file called apikey.txt? (y/n):");
            String input = scanner.nextLine();
            if(input.equals("y")){
                File file = new File("apikey.txt");
                if(file.exists()){
                    BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(file, true));
                    bufferedWriter.write(apiKey);
                    bufferedWriter.newLine();
                    bufferedWriter.close();
                    System.out.println("The api key " + apiKey + " has been saved to apikey.txt");
                    break;
                }else{
                    BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter("apikey.txt"));
                    bufferedWriter.write(apiKey);
                    bufferedWriter.newLine();
                    bufferedWriter.close();
                    System.out.println("The api key " + apiKey + " has been saved to apikey.txt");
                    break;
                }
            }else if(input.equals("n")){
                break;
            }else{
                System.out.println("Invalid choice. Please try again.");
            }
        }
        System.out.println("Api key has been successfully entered.");
    }
    private static void loadAPIKEYFromTextFile() throws IOException {
        File file = new File("apikey.txt");
        if(file.exists()){
            BufferedReader reader = new BufferedReader(new FileReader(file));
            while(reader.ready()){
                apiKey = reader.readLine();
                break;
            }
            System.out.println("Api key has been successfully loaded from apikey.txt");
        }else{
            System.out.println("File does not exist!");
        }
    }
    private static void getInputForCity(){
        while(true){
            Scanner scanner = new Scanner(System.in);
            System.out.println("Enter city: ");
            boolean hasInt = scanner.hasNextInt();
            if(hasInt){
                System.out.println("Please enter a city.");
                scanner.nextInt();
                scanner.nextLine();
            }else{
                if(!city.equals("")){
                    previousCity = city;
                    city = scanner.nextLine();
                    response = "";
                }else{
                    city = scanner.nextLine();
                    response = "";
                }
                System.out.println("City has been successfully entered.");
                break;
            }
        }
    }
    private static String getValue(String object, String response){
        int index = response.indexOf(object);
        int valueIndex = -1;
        if(response.charAt(index + (object.length()-1) + 3) == '"'){
            valueIndex = index + (object.length()-1) + 4;
        }else{
            valueIndex = index + (object.length()-1) + 3;
        }

        String value = "";
        for(int i=valueIndex; i<response.length(); i++){
            if(response.charAt(i) == '"' || response.charAt(i) == ','){
                break;
            }
            value += response.charAt(i);

        }
        return value;
    }
    private static String getWeatherInfornation() throws IOException {
        URL weatherURL = new URL("http://api.weatherstack.com/current?access_key=" + apiKey + "&query=" + city);
        HttpURLConnection weatherConnection = (HttpURLConnection) weatherURL.openConnection();
        weatherConnection.setRequestMethod("GET");
        weatherConnection.connect();
        int response = weatherConnection.getResponseCode();
        if (response == 200) {
            BufferedReader reader = new BufferedReader(new InputStreamReader(weatherConnection.getInputStream()));
            String stringResponse = "";
            while (reader.ready()) {
                stringResponse += reader.readLine();
            }
            return stringResponse;


        } else {
            System.out.println("There was an error getting the weather data.");
            return "";
        }
    }
    private static void displayWeatherInformation(String response){
        //display country
        String value = getValue("country", response);
        System.out.println("Country: " + value);
        //display region
        value = getValue("region", response);
        System.out.println("Region: " + value);
        //display temperature in celsius
        value = getValue("temperature", response);
        double tempFahrenheight = (Integer.parseInt(value) * 1.8) + 32;

        System.out.println("Temperature: " + value + "\u00b0C" + " or " + tempFahrenheight + "\u00b0F");
        //display temperature in fahrenheit


    }
}
