package com.customer.repository;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import org.springframework.http.*;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.models.Customer;
import com.utils.SecurityUtility;

@Repository
public class PagemainRepository {
	
	

	public List<Province> getProvinces() {
	    try {
	        String apiUrl = "https://online-gateway.ghn.vn/shiip/public-api/master-data/province";
	        
	        HttpHeaders headers = new HttpHeaders();
	        headers.set("Token", "858b9bf9-ab01-11ef-81b6-5e2e958f07fa");
	        headers.setContentType(MediaType.APPLICATION_JSON);
	        
	        HttpEntity<String> entity = new HttpEntity<>(headers);
	        
	        RestTemplate restTemplate = new RestTemplate();
	        
	        // Thêm log để xem response JSON thực tế
	        ResponseEntity<String> rawResponse = restTemplate.exchange(
	            apiUrl,
	            HttpMethod.GET,
	            entity,
	            String.class
	        );
	        System.out.println("Raw Response: " + rawResponse.getBody());
	        
	        // Gọi API với model Province
	        ResponseEntity<GHNResponse<List<Province>>> response = restTemplate.exchange(
	            apiUrl,
	            HttpMethod.GET,
	            entity,
	            new ParameterizedTypeReference<GHNResponse<List<Province>>>() {}
	        );
	        
	        System.out.println("Response Code: " + response.getStatusCode());
	        if (response.getBody() != null) {
	            System.out.println("GHN Code: " + response.getBody().getCode());
	            System.out.println("GHN Message: " + response.getBody().getMessage());
	        }
	        
	        if (response.getBody() != null && response.getBody().getData() != null) {
	            List<Province> provinces = response.getBody().getData();
	            provinces.forEach(p -> {
	                System.out.println("Province ID: " + p.getProvinceId());
	                System.out.println("Province Name: " + p.getProvinceName());
	                System.out.println("Province Code: " + p.getCode());
	                System.out.println("----------------");
	            });
	            return provinces;
	        }
	        
	        return new ArrayList<>();
	        
	    } catch (Exception e) {
	        System.out.println("Error: " + e.getMessage());
	        e.printStackTrace();
	        return new ArrayList<>();
	    }
	}
}
class GHNResponse<T> {
    private int code;
    private String message;
    private T data;
	public int getCode() {
		return code;
	}
	public void setCode(int code) {
		this.code = code;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public T getData() {
		return data;
	}
	public void setData(T data) {
		this.data = data;
	}
    
    
}
class Province {
    @JsonProperty("ProvinceID")
    private Integer provinceId;
    
    @JsonProperty("ProvinceName")
    private String provinceName;
    
    @JsonProperty("CountryID")
    private Integer countryId;
    
    @JsonProperty("Code")
    private String code;
    
    @JsonProperty("NameExtension")
    private List<String> nameExtension;

    // Getters và Setters
    public Integer getProvinceId() {
        return provinceId;
    }

    public void setProvinceId(Integer provinceId) {
        this.provinceId = provinceId;
    }

    public String getProvinceName() {
        return provinceName;
    }

    public void setProvinceName(String provinceName) {
        this.provinceName = provinceName;
    }

	public Integer getCountryId() {
		return countryId;
	}

	public void setCountryId(Integer countryId) {
		this.countryId = countryId;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public List<String> getNameExtension() {
		return nameExtension;
	}

	public void setNameExtension(List<String> nameExtension) {
		this.nameExtension = nameExtension;
	}

    
}
