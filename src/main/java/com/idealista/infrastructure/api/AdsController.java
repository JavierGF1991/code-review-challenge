package com.idealista.infrastructure.api;

import java.util.List;

import com.idealista.application.AdsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AdsController {

    @Autowired
    private AdsService adsService;

    @PostMapping("/ads/save")
    public ResponseEntity<Void> save() {
    	adsService.save();
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
    
    @GetMapping("/ads/all")
    public ResponseEntity<List<?>> findAllAds() {
    	List<QualityAd> vOqa = adsService.findAllAds();
		if (vOqa.isEmpty()) {
			return ResponseEntity.notFound().build();
		}
        return ResponseEntity.ok(vOqa);
    }

    @GetMapping("/ads/quality")
    public ResponseEntity<List<?>> qualityListing() {
    	List<QualityAd> vOqa = adsService.findQualityAds();
		if (vOqa.isEmpty()) {
			return ResponseEntity.notFound().build();
		}
        return ResponseEntity.ok(vOqa);
    }

    @GetMapping("/ads/public")
    public ResponseEntity<List<?>> publicListing() {
    	List<PublicAd> vOqa = adsService.findPublicAds();
		if (vOqa.isEmpty()) {
			return ResponseEntity.notFound().build();
		}
        return ResponseEntity.ok(vOqa);
    }
}
