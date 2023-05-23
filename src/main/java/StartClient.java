import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import model.Sensor;
import org.knowm.xchart.SwingWrapper;
import org.knowm.xchart.XYChart;
import org.knowm.xchart.XYChartBuilder;
import org.knowm.xchart.style.markers.SeriesMarkers;
import org.springframework.http.HttpEntity;
import org.springframework.web.client.RestTemplate;
import model.Measurement;

import java.util.*;

public class StartClient {

    public static final double MIN_TEMPERATURE = -100;
    public static final double MAX_TEMPERATURE = 100;
    private static String url = "http://localhost:8080/sensors/registration";
    private static final RestTemplate restTemplate = new RestTemplate();
    private static final Map<String, Object> jsonToSend = new HashMap<>();
    private static Sensor sensor = new Sensor("Sensor3");
    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static void main(String[] args) {

        sensor = registerSensor();
        startMeasuring();
        List<Measurement> measurements = getAllMeasurements();
        createAndShowChart(measurements);
    }

    public static Sensor registerSensor(){
        jsonToSend.put("name",sensor.getName());
        HttpEntity<Map<String,Object>> response = new HttpEntity<>(jsonToSend);
        restTemplate.postForObject(url,response,String.class);
        return sensor;
    }

    public static void startMeasuring(){
        url = "http://localhost:8080/measurement/add";
        for(int i = 0; i < 1000;i++){
            jsonToSend.clear();
            jsonToSend.put("value",100-Math.random()*200);
            jsonToSend.put("raining",getRandomBoolean());
            jsonToSend.put("sensor",sensor);
            restTemplate.postForObject(url,jsonToSend, String.class);
        }
    }

    public static List<Measurement> getAllMeasurements(){
        url = "http://localhost:8080/measurements";
        String json =  restTemplate.getForObject(url,String.class);
        try {
            return objectMapper.readValue(json, new TypeReference<List<Measurement>>(){});
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

    }

    public static void createAndShowChart(List<Measurement> measurements){
       XYChart chart = createChart();
       fill(chart,measurements);
       show(chart);
    }
    public static XYChart createChart(){
        XYChart chart = new XYChartBuilder().xAxisTitle("Days").yAxisTitle("Temperature").width(1200).height(150).build();
        chart.getStyler().setYAxisMin(MIN_TEMPERATURE);
        chart.getStyler().setYAxisMax(MAX_TEMPERATURE);
        return chart;
    }

    public static void fill(XYChart chart,List<Measurement> measurements){
        double[] values = measurements.stream().filter(e->e.getSensor().getName().equals("Sensor3")).mapToDouble(Measurement::getValue).toArray();
        chart.addSeries("All sensors",values).setMarker(SeriesMarkers.NONE);

    }
    public static void show(XYChart chart){
        new SwingWrapper<>(chart).displayChart();
    }

    public static boolean getRandomBoolean(){
        return (int)(Math.random()*10)>5;
    }
}
