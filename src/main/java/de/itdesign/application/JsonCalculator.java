package de.itdesign.application;


import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;

public class JsonCalculator {


    public static void main(String[] args) {

        //Don't change this part
        if (args.length == 3) {
            //Path to the data file, e.g. data/data.xml
            final String DATA_FILE = args[0];
            //Path to the data file, e.g. operations/operations.xml
            final String OPERATIONS_FILE = args[1];
            //Path to the output file
            final String OUTPUT_FILE = args[2];

            performAllOperations(DATA_FILE, OPERATIONS_FILE, OUTPUT_FILE);
        } else {
            System.exit(1);
        }

    }

    /**
     * This method performs all operations
     *
     * @param DATA_FILE       path to data file
     * @param OPERATIONS_FILE path to operations file
     * @param OUTPUT_FILE     path to generated output
     */
    private static void performAllOperations(String DATA_FILE, String OPERATIONS_FILE, String OUTPUT_FILE) {

        JSONParser jsonParser = new JSONParser();
        JSONArray outputArray = new JSONArray();

        try {
            //transform JSON with operations to an JSONArray
            JSONObject jsonObjectOperations = (JSONObject) jsonParser.parse(new FileReader(OPERATIONS_FILE));
            JSONArray operationArray = (JSONArray) jsonObjectOperations.get("operations");

            //transform JSON with data to an JSONArray
            JSONObject jsonObjectData = (JSONObject) jsonParser.parse(new FileReader(DATA_FILE));
            JSONArray dataArray = (JSONArray) jsonObjectData.get("entries");

            if (dataArray.isEmpty())
                throw new IllegalArgumentException("Data File has no entries!");

            //perform every operation individually
            for (int i = 0; i < operationArray.size(); i++) {
                JSONObject jsonObj = (JSONObject) operationArray.get(i);
                outputArray.add(peformOperation(jsonObj, dataArray));
            }
            //write output file
            FileWriter file = new FileWriter(OUTPUT_FILE);
            JSONArray.writeJSONString(outputArray, file);
            file.close();

        } catch (IOException e) {
            throw new IllegalArgumentException("Couldn't read or write to one of these files: "
                    + DATA_FILE + " " + OPERATIONS_FILE + " " + OUTPUT_FILE);
        } catch (ParseException e) {
            throw new IllegalArgumentException("Couldn't parse a file. Maybe the JSON-File is corrupted." +
                    " Please make sure the JSON-Files are correct");
        }
    }

    /**
     * @param operationObject JSONObject with one operation
     * @param dataArray       whole data array to be operated
     * @return an JSONObject with name and roundedValue from given operation
     * @throws IOException
     * @throws ParseException
     */
    private static JSONObject peformOperation(JSONObject operationObject, JSONArray dataArray)
            throws IOException, ParseException {


        JSONObject returnObj = new JSONObject();

        List<String> fields = ((JSONArray) operationObject.get("field")).stream().toList();

        String operation = (String) operationObject.get("function");

        //perform given operation only on values given by filter and fields
        switch (operation) {
            case "average" -> returnObj.put("roundedValue", String.format(Locale.ENGLISH, "%.2f",
                    dataArray.stream()
                            //apply filter to name
                            .filter(obj ->
                                    Pattern.compile((String) operationObject.get("filter"))
                                            .matcher((String) ((JSONObject) obj).get("name")).find())
                            //generate map with all values
                            .map(obj -> getValueFromField((JSONObject) obj, fields))
                            //erase null values
                            .filter(d -> d != null)
                            //perform operation
                            .mapToDouble(d -> (double) d).average().getAsDouble()));
            case "min" -> returnObj.put("roundedValue", String.format(Locale.ENGLISH, "%.2f",
                    dataArray.stream()
                            .filter(obj ->
                                    Pattern.compile((String) operationObject.get("filter"))
                                            .matcher((String) ((JSONObject) obj).get("name")).find())
                            .map(obj -> getValueFromField((JSONObject) obj, fields))
                            .filter(d -> d != null)
                            .mapToDouble(d -> (double) d).min().orElse(0.00)));
            case "max" -> returnObj.put("roundedValue", String.format(Locale.ENGLISH, "%.2f",
                    dataArray.stream()
                            .filter(obj ->
                                    Pattern.compile((String) operationObject.get("filter"))
                                            .matcher((String) ((JSONObject) obj).get("name")).find())
                            .map(obj -> getValueFromField((JSONObject) obj, fields))
                            .filter(d -> d != null)
                            .mapToDouble(d -> (double) d).max().orElse(0.00)));
            case "sum" -> returnObj.put("roundedValue", String.format(Locale.ENGLISH, "%.2f",
                    dataArray.stream()
                            .filter(obj ->
                                    Pattern.compile((String) operationObject.get("filter"))
                                            .matcher((String) ((JSONObject) obj).get("name")).find())
                            .map(obj -> getValueFromField((JSONObject) obj, fields))
                            .filter(d -> d != null)
                            .mapToDouble(d -> (double) d).sum()));
            default -> throw new IllegalArgumentException("Operation: " + operation + " invalid!");
        }
        returnObj.put("name", operationObject.get("name"));
        return returnObj;
    }

    /**
     * Helper Function to get required Value from an data object
     *
     * @param objFromData Object from dataset to get the value from
     * @param fields      list of fields to get to value
     * @return value as Double or null if required field isn't present
     */
    private static Double getValueFromField(JSONObject objFromData, List<String> fields) {
        if (fields.isEmpty()) return null;
        Object currentValueObj = objFromData.get(fields.get(0));
        if (currentValueObj == null) return null;
        for (int i = 1; i < fields.size(); i++) {
            if (currentValueObj instanceof JSONObject) {
                currentValueObj = ((JSONObject) currentValueObj).get(fields.get(i));
            } else {
                return null;
            }
        }


        return currentValueObj == null ? null : Double.parseDouble(currentValueObj.toString());
    }
}
