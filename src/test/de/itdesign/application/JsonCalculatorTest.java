package de.itdesign.application;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.junit.Assert;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.FileReader;
import java.util.HashMap;


class JsonCalculatorTest {
    final JSONParser jsonParser = new JSONParser();

    @Test
    void testWholeMainMethod() {
        JsonCalculator.main(new String[]{"data.json", "operations.json", "output2.json"});

        try {
            JSONArray jsonArray = (JSONArray) jsonParser.parse(new FileReader("output2.json"));
            JSONArray jsonArrayExpected = (JSONArray) jsonParser.parse(new FileReader("output.json"));
            Assert.assertEquals(jsonArrayExpected, jsonArray);
        } catch (Exception e) {
            throw new AssertionError();
        }
    }

    @Test
    void testCorruptedFile() {
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> JsonCalculator.main(new String[]{"corruptedData.json", "operations.json", "output2.json"}));
    }

    @Test
    void testMissingFile() {
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> JsonCalculator.main(new String[]{"", "operations.json", "output2.json"}));
    }

    @Test
    void testInvalidOperation() {
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> JsonCalculator.main(new String[]{"data.json", "src/test/de/itdesign/application/resources/invalidOperation.json", "output2.json"}));
    }

    @Test
    void testEdgeOperations() {
        JsonCalculator.main(new String[]{"src/test/de/itdesign/application/resources/testData.json",
                "src/test/de/itdesign/application/resources/testOperations.json", "output2.json"});

        try {
            JSONArray outputArray = (JSONArray) jsonParser.parse(new FileReader("output2.json"));
            HashMap<String, String> nameToValueMap = new HashMap<>();
            JSONObject outputObject = (JSONObject) outputArray.get(0);
            if (outputObject != null) {
                for (int i = 0; i < outputArray.size(); i++) {
                    JSONObject currentObj = (JSONObject) outputArray.get(i);
                    nameToValueMap.put((String) currentObj.get("name"), (String) currentObj.get("roundedValue"));
                }
            }
            Assert.assertEquals("1.00", nameToValueMap.get("uniqueFields"));
            Assert.assertEquals("2510.00", nameToValueMap.get("nestedFields"));
            Assert.assertEquals("0.00", nameToValueMap.get("noValuesPossible"));
        } catch (Exception e) {
            throw new AssertionError();
        }
    }

    @Test
    void testOtherDataSet() {
        JsonCalculator.main(new String[]{"src/test/de/itdesign/application/resources/testData2.json",
                "src/test/de/itdesign/application/resources/testOperationsData2.json", "output2.json"});

        try {
            JSONArray outputArray = (JSONArray) jsonParser.parse(new FileReader("output2.json"));
            HashMap<String, String> nameToValueMap = new HashMap<>();
            JSONObject outputObject = (JSONObject) outputArray.get(0);
            if (outputObject != null) {
                for (int i = 0; i < outputArray.size(); i++) {
                    JSONObject currentObj = (JSONObject) outputArray.get(i);
                    nameToValueMap.put((String) currentObj.get("name"), (String) currentObj.get("roundedValue"));
                }
            }
            Assert.assertEquals("10.00", nameToValueMap.get("bestForJob"));
            Assert.assertEquals("1.00", nameToValueMap.get("bestCV"));
            Assert.assertEquals("27.00", nameToValueMap.get("averageAge"));
        } catch (Exception e) {
            throw new AssertionError();
        }
    }
}