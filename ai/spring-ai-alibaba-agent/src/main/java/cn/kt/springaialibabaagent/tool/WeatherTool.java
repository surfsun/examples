package cn.kt.springaialibabaagent.tool;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.ai.chat.model.ToolContext;
import org.springframework.ai.tool.annotation.ToolParam;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.List;
import java.util.Objects;
import java.util.StringJoiner;
import java.util.function.BiFunction;

public class WeatherTool implements BiFunction<String, ToolContext, String> {
    private static final HttpClient HTTP_CLIENT = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(5))
            .build();
    private static final ObjectMapper MAPPER = new ObjectMapper();

    @Override
    public String apply(@ToolParam(description = "City or location name") String city, ToolContext toolContext) {
        if (city == null || city.trim().isEmpty()) {
            return "城市不能为空。请提供城市或地点名称。";
        }

        try {
            Location location = geocode(city.trim());
            if (location == null) {
                return "未找到该城市，请换一个更具体的名称。";
            }

            Forecast forecast = fetchCurrentWeather(location);
            if (forecast == null || forecast.current == null) {
                return "未能获取到当前天气数据，请稍后再试。";
            }

            String place = formatPlace(location);
            String temperature = formatValue(forecast.current.temperature_2m, forecast.currentUnits.temperature_2m);
            String humidity = formatValue(forecast.current.relative_humidity_2m, forecast.currentUnits.relative_humidity_2m);
            String wind = formatValue(forecast.current.wind_speed_10m, forecast.currentUnits.wind_speed_10m);
            String code = forecast.current.weather_code == null ? "未知" : String.valueOf(forecast.current.weather_code);
            String time = forecast.current.time == null ? "未知时间" : forecast.current.time;
            String timezone = forecast.timezone == null ? "" : "（" + forecast.timezone + "）";

            return new StringJoiner("，", "当前天气（" + place + "）：", "")
                    .add("温度 " + temperature)
                    .add("相对湿度 " + humidity)
                    .add("风速 " + wind)
                    .add("天气代码 " + code)
                    .add("时间 " + time + timezone)
                    .toString();
        } catch (Exception e) {
            return "获取天气失败：" + e.getMessage();
        }
    }

    private static Location geocode(String city) throws IOException, InterruptedException {
        String encoded = URLEncoder.encode(city, StandardCharsets.UTF_8);
        String url = "https://geocoding-api.open-meteo.com/v1/search?name=" + encoded
                + "&count=1&language=zh&format=json";
        HttpRequest request = HttpRequest.newBuilder(URI.create(url))
                .timeout(Duration.ofSeconds(10))
                .GET()
                .build();
        HttpResponse<String> response = HTTP_CLIENT.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() / 100 != 2) {
            throw new IOException("地理编码接口响应异常，HTTP " + response.statusCode());
        }

        GeocodingResponse geocodingResponse = MAPPER.readValue(response.body(), GeocodingResponse.class);
        if (geocodingResponse == null || geocodingResponse.results == null || geocodingResponse.results.isEmpty()) {
            return null;
        }
        GeocodingResult result = geocodingResponse.results.getFirst();
        if (result.latitude == null || result.longitude == null) {
            return null;
        }

        return new Location(result.name, result.admin1, result.country, result.latitude, result.longitude);
    }

    private static Forecast fetchCurrentWeather(Location location) throws IOException, InterruptedException {
        String url = "https://api.open-meteo.com/v1/forecast?latitude=" + location.latitude
                + "&longitude=" + location.longitude
                + "&current=temperature_2m,relative_humidity_2m,weather_code,wind_speed_10m"
                + "&timezone=auto";
        HttpRequest request = HttpRequest.newBuilder(URI.create(url))
                .timeout(Duration.ofSeconds(10))
                .GET()
                .build();
        HttpResponse<String> response = HTTP_CLIENT.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() / 100 != 2) {
            throw new IOException("天气接口响应异常，HTTP " + response.statusCode());
        }

        ForecastResponse forecastResponse = MAPPER.readValue(response.body(), ForecastResponse.class);
        if (forecastResponse == null) {
            return null;
        }
        return new Forecast(forecastResponse.current, forecastResponse.current_units, forecastResponse.timezone);
    }

    private static String formatPlace(Location location) {
        StringJoiner joiner = new StringJoiner(", ");
        if (location.name != null) {
            joiner.add(location.name);
        }
        if (location.admin1 != null && !location.admin1.isBlank()) {
            joiner.add(location.admin1);
        }
        if (location.country != null && !location.country.isBlank()) {
            joiner.add(location.country);
        }
        return joiner.toString();
    }

    private static String formatValue(Number value, String unit) {
        String safeUnit = unit == null ? "" : unit;
        return Objects.requireNonNullElse(value, "未知") + safeUnit;
    }

    private record Location(String name, String admin1, String country, double latitude, double longitude) {}

    private record Forecast(Current current, CurrentUnits currentUnits, String timezone) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    private record GeocodingResponse(List<GeocodingResult> results) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    private record GeocodingResult(String name, String admin1, String country, Double latitude, Double longitude) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    private record ForecastResponse(Current current, CurrentUnits current_units, String timezone) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    private record Current(Double temperature_2m,
                           Double relative_humidity_2m,
                           Double wind_speed_10m,
                           Integer weather_code,
                           String time) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    private record CurrentUnits(String temperature_2m,
                                String relative_humidity_2m,
                                String wind_speed_10m) {}
}
