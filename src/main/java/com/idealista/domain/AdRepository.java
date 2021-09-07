package com.idealista.domain;

import java.util.List;

public interface AdRepository {
	
    List<Ad> findAllAds();

    List<Ad> findRelevantAds();

    List<Ad> findIrrelevantAds();

	void save();
}
