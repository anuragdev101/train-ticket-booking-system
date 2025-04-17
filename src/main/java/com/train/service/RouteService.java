package com.train.service;

import com.train.model.Route;
import org.springframework.stereotype.Service;
import jakarta.annotation.PostConstruct;
import java.util.*;

@Service
public class RouteService {
    private final Map<String, List<Route>> routes = new HashMap<>();
    
    @PostConstruct
    public void initializeRoutes() {
        // Initialize some sample routes with prices
        addRoute("London", "Paris", 20.0);
        addRoute("London", "Brussels", 25.0);
        addRoute("Paris", "Amsterdam", 30.0);
        addRoute("Brussels", "Amsterdam", 15.0);
        addRoute("Paris", "Brussels", 18.0);
        addRoute("London", "Amsterdam", 35.0);
    }
    
    private void addRoute(String from, String to, double price) {
        // Add route in both directions
        routes.computeIfAbsent(from, k -> new ArrayList<>())
              .add(new Route(from, to, price));
        routes.computeIfAbsent(to, k -> new ArrayList<>())
              .add(new Route(to, from, price));
    }
    
    public Set<String> getAllStations() {
        return routes.keySet();
    }
    
    public Optional<Route> findRoute(String from, String to) {
        List<Route> availableRoutes = routes.getOrDefault(from, Collections.emptyList());
        return availableRoutes.stream()
                .filter(route -> route.getTo().equals(to))
                .findFirst();
    }
    
    public double calculatePrice(String from, String to) {
        return findRoute(from, to)
                .map(Route::getBasePrice)
                .orElseThrow(() -> new IllegalArgumentException(
                    String.format("No route found from %s to %s", from, to)));
    }
} 