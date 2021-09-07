package com.idealista.application;

import com.idealista.domain.*;
import com.idealista.infrastructure.api.PublicAd;
import com.idealista.infrastructure.api.QualityAd;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class AdsServiceImpl implements AdsService {

    @Autowired
    private AdRepository adRepository;

    @Override
    public List<PublicAd> findPublicAds() {
        List<Ad> ads = adRepository.findRelevantAds();
        ads.sort(Comparator.comparing(Ad::getScore).reversed());

        List<PublicAd> result = new ArrayList<>();
        for (Ad ad: ads) {
            PublicAd publicAd = new PublicAd();
            publicAd.setDescription(ad.getDescription());
            publicAd.setGardenSize(ad.getGardenSize());
            publicAd.setHouseSize(ad.getHouseSize());
            publicAd.setId(ad.getId());
            publicAd.setPictureUrls(ad.getPictures().stream().map(Picture::getUrl).collect(Collectors.toList()));
            publicAd.setTypology(ad.getTypology().name());

            result.add(publicAd);
        }
        
        return result;
    }
    
    @Override
    public List<QualityAd> findQualityAds() {
        List<Ad> ads = adRepository.findIrrelevantAds();

        List<QualityAd> result = new ArrayList<>();
        for (Ad ad: ads) {
            QualityAd qualityAd = new QualityAd();
            qualityAd.setDescription(ad.getDescription());
            qualityAd.setGardenSize(ad.getGardenSize());
            qualityAd.setHouseSize(ad.getHouseSize());
            qualityAd.setId(ad.getId());
            qualityAd.setPictureUrls(ad.getPictures().stream().map(Picture::getUrl).collect(Collectors.toList()));
            qualityAd.setTypology(ad.getTypology().name());
            qualityAd.setScore(ad.getScore());
            qualityAd.setIrrelevantSince(ad.getIrrelevantSince());

            result.add(qualityAd);
        }

        return result;
    }
    

    @Override
    public List<QualityAd> findAllAds() {
        List<Ad> ads = adRepository.findAllAds();

        List<QualityAd> result = new ArrayList<>();
        for (Ad ad: ads) {
            QualityAd qualityAd = new QualityAd();
            qualityAd.setDescription(ad.getDescription());
            qualityAd.setGardenSize(ad.getGardenSize());
            qualityAd.setHouseSize(ad.getHouseSize());
            qualityAd.setId(ad.getId());
            qualityAd.setPictureUrls(ad.getPictures().stream().map(Picture::getUrl).collect(Collectors.toList()));
            qualityAd.setTypology(ad.getTypology().name());
            qualityAd.setScore(ad.getScore());
            qualityAd.setIrrelevantSince(ad.getIrrelevantSince());

            result.add(qualityAd);
        }

        return result;
    }

    @Override
    public void save() {
        adRepository.save();     
    }
}
