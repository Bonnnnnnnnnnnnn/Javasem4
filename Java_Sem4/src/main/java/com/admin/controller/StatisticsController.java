package com.admin.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.admin.repository.StatisticsRepository;
import com.customer.repository.GHNService;
import com.customer.repository.ShoppingpageRepository;
import com.models.PageView;
import com.models.Product;
import com.utils.Views;


@Controller
@RequestMapping("/admin/statistics")
public class StatisticsController {
    
    @Autowired
    StatisticsRepository repstat;
    
    @Autowired
    ShoppingpageRepository repspp;


    @GetMapping("/showpage")
    public String showpage(Model model) {
    	int[] idCategories = {};
		int[] idBrands = {};
		String[] statuses = { "NewRelease", "Active", "OutOfStock", "Inactive" };
        model.addAttribute("products", repspp.findAllnopaging(new PageView(), "", idCategories, idBrands, statuses));
        repstat.getGrossProfitAnalysis("monthly");
    
        return Views.ADMIN_STATISTICSPAGE;
    }

    @GetMapping("/api/revenue/{period}")
    @ResponseBody
    public Map<String, Object> getRevenue(
        @PathVariable String period,
        @RequestParam(required = false) Long productId) {
        
        List<Map<String, Object>> data = repstat.getRevenue(period, productId);
        
        List<String> labels = new ArrayList<>();
        List<Double> values = new ArrayList<>();
        List<Double> totalPeriodRevenue = new ArrayList<>();
        List<Double> percentage = new ArrayList<>();

        for (Map<String, Object> row : data) {
            labels.add(row.get("timePeriod").toString());
            values.add(((Number)row.get("SelectedRevenue")).doubleValue());
            totalPeriodRevenue.add(((Number)row.get("TotalRevenue")).doubleValue());
            percentage.add(((Number)row.get("SelectedPercentage")).doubleValue());
        }

        Map<String, Object> result = new HashMap<>();
        result.put("labels", labels);
        result.put("values", values);
        result.put("totalPeriodRevenue", totalPeriodRevenue);
        result.put("percentage", percentage);

        return result;
    }

  
    @GetMapping("/products/{period}")
    public ResponseEntity<Map<String, Object>> getProductStats(
            @PathVariable String period,
            @RequestParam(required = false) Integer productId) {
        try {
            Map<String, Object> response = new HashMap<>();
            
           
            List<Map<String, Object>> summaryList = repstat.getProductSummary(period);
            
            
            Map<String, Object> latestSummary = summaryList.isEmpty() ? 
                new HashMap<>() : summaryList.get(0);
            
           
            List<Map<String, Object>> topProducts = repstat.getTopProducts(period);
            
            
            response.put("summary", latestSummary);  
            response.put("summaryTrend", summaryList); 
            response.put("topProducts", topProducts);
            
            response.put("period", period);
            response.put("totalPeriods", summaryList.size());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of(
                        "error", "Failed to fetch product statistics",
                        "message", e.getMessage()
                    ));
        }
    }
    @GetMapping("/api/warehouse-revenue/{period}")
    @ResponseBody
    public Map<String, Object> getWarehouseRevenue(@PathVariable String period) {
        List<Map<String, Object>> rawData = repstat.getWarehouseRevenue(period);
        
        // Get unique warehouses and time periods
        Set<String> warehouses = new TreeSet<>();
        Set<String> timePeriods = new TreeSet<>();
        
        for (Map<String, Object> row : rawData) {
            warehouses.add(row.get("warehouseName").toString());
            timePeriods.add(row.get("timePeriod").toString());
        }

        // Calculate total revenue for each period
        Map<String, Double> periodTotals = new HashMap<>();
        for (Map<String, Object> row : rawData) {
            String periodd = row.get("timePeriod").toString();
            Double revenue = ((Number)row.get("NetRevenue")).doubleValue();
            periodTotals.merge(periodd, revenue, Double::sum);
        }

        Map<String, Object> result = new HashMap<>();
        result.put("data", rawData);  // Original data for table
        result.put("warehouses", new ArrayList<>(warehouses));  // List of warehouses
        result.put("labels", new ArrayList<>(timePeriods));  // Time periods
        result.put("periodTotals", periodTotals);  // Total revenue per period

        return result;
    }
    
    @GetMapping("/province-revenue/{period}")
    public ResponseEntity<List<Map<String, Object>>> getProvinceRevenue(
            @PathVariable String period) {
        return ResponseEntity.ok(repstat.getProvinceRevenue(period));
    }
    
    
    
    
}