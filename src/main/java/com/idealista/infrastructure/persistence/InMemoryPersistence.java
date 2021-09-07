package com.idealista.infrastructure.persistence;

import com.idealista.domain.*;

import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
public class InMemoryPersistence implements AdRepository {

	private List<AdVO> adsVO;
	private List<PictureVO> pictures;

	public InMemoryPersistence() {
		adsVO = new ArrayList<AdVO>();
		adsVO.add(new AdVO(1, "CHALET", "Este piso es una ganga, compra, compra, COMPRA!!!!!",
				Collections.<Integer>emptyList(), 300, null, null, null));
		adsVO.add(new AdVO(2, "FLAT",
				"Nuevo ático céntrico recién reformado. No deje pasar la oportunidad y adquiera este ático de lujo",
				Arrays.asList(4), 300, null, null, null));
		adsVO.add(new AdVO(3, "CHALET", "", Arrays.asList(2), 300, null, null, null));
		adsVO.add(new AdVO(4, "FLAT", "Ático céntrico muy luminoso y recién reformado, parece nuevo", Arrays.asList(5),
				300, null, null, null));
		adsVO.add(new AdVO(5, "FLAT", "Pisazo,", Arrays.asList(3, 8), 300, null, null, null));
		adsVO.add(new AdVO(6, "GARAGE", "", Arrays.asList(6), 300, null, null, null));
		adsVO.add(new AdVO(7, "GARAGE", "Garaje en el centro de Albacete", Collections.<Integer>emptyList(), 300, null,
				null, null));
		adsVO.add(new AdVO(8, "CHALET",
				"Maravilloso chalet situado en lAs afueras de un pequeño pueblo rural. El entorno es espectacular, las vistas magníficas. ¡Cómprelo ahora!",
				Arrays.asList(1, 7), 300, null, null, null));

		pictures = new ArrayList<PictureVO>();
		pictures.add(new PictureVO(1, "http://www.idealista.com/pictures/1", "SD"));
		pictures.add(new PictureVO(2, "http://www.idealista.com/pictures/2", "HD"));
		pictures.add(new PictureVO(3, "http://www.idealista.com/pictures/3", "SD"));
		pictures.add(new PictureVO(4, "http://www.idealista.com/pictures/4", "HD"));
		pictures.add(new PictureVO(5, "http://www.idealista.com/pictures/5", "SD"));
		pictures.add(new PictureVO(6, "http://www.idealista.com/pictures/6", "SD"));
		pictures.add(new PictureVO(7, "http://www.idealista.com/pictures/7", "SD"));
		pictures.add(new PictureVO(8, "http://www.idealista.com/pictures/8", "HD"));
	}

	@Override
	public List<Ad> findAllAds() {

		return adsVO.stream().map(this::mapToDomain).collect(Collectors.toList());
	}

	@Override
	public List<Ad> findRelevantAds() {

		return adsVO.stream().filter(x -> x.getScore() >= Constants.FORTY).map(this::mapToDomain)
				.collect(Collectors.toList());
	}

	@Override
	public List<Ad> findIrrelevantAds() {
		return adsVO.stream().filter(x -> x.getScore() < Constants.FORTY).map(this::mapToDomain)
				.collect(Collectors.toList());
	}

	private Ad mapToDomain(AdVO adVO) {
		return new Ad(adVO.getId(), Typology.valueOf(adVO.getTypology()), adVO.getDescription(),
				adVO.getPictures().stream().map(this::mapToDomain).collect(Collectors.toList()), adVO.getHouseSize(),
				adVO.getGardenSize(), adVO.getScore(), adVO.getIrrelevantSince());
	}

	@Override
	public void save() {
		List<Ad> ads = adsVO.stream().map(this::mapToDomain).collect(Collectors.toList());
		for (Ad ad : ads) {
			calculateScore(ad);
			adsVO.removeIf(x -> x.getId().equals(ad.getId()));
			adsVO.add(mapToPersistence(ad));
			ad.getPictures().forEach(this::save);
		}
	}

	private void save(Picture picture) {
		pictures.removeIf(x -> x.getId().equals(picture.getId()));
		pictures.add(mapToPersistence(picture));
	}

	private Picture mapToDomain(Integer pictureId) {
		return pictures.stream().filter(x -> x.getId().equals(pictureId)).findFirst()
				.map(pictureVO -> new Picture(pictureVO.getId(), pictureVO.getUrl(),
						Quality.valueOf(pictureVO.getQuality())))
				.orElse(null);
	}

	private AdVO mapToPersistence(Ad ad) {
		return new AdVO(ad.getId(), ad.getTypology().name(), ad.getDescription(),
				ad.getPictures().stream().map(Picture::getId).collect(Collectors.toList()), ad.getHouseSize(),
				ad.getGardenSize(), ad.getScore(), ad.getIrrelevantSince());
	}

	private PictureVO mapToPersistence(Picture picture) {
		return new PictureVO(picture.getId(), picture.getUrl(), picture.getQuality().name());
	}

	private void calculateScore(Ad ad) {
		int score = Constants.ZERO;

		// Calcular puntuación por fotos
		if (ad.getPictures().isEmpty()) {
			score -= Constants.TEN; // Si no hay fotos restamos 10 puntos
		} else {
			for (Picture picture : ad.getPictures()) {
				if (Quality.HD.equals(picture.getQuality())) {
					score += Constants.TWENTY; // Cada foto en alta definición aporta 20 puntos
				} else {
					score += Constants.TEN; // Cada foto normal aporta 10 puntos
				}
			}
		}

		// Calcular puntuación por descripción
		Optional<String> optDesc = Optional.ofNullable(ad.getDescription());

		if (optDesc.isPresent()) {
			String description = optDesc.get();

			if (!description.isEmpty()) {
				score += Constants.FIVE;
			}

			List<String> wds = Arrays.asList(description.split(" ")); // número de palabras
			if (Typology.FLAT.equals(ad.getTypology())) {
				if (wds.size() >= Constants.TWENTY && wds.size() <= Constants.FORTY_NINE) {
					score += Constants.TEN;
				}

				if (wds.size() >= Constants.FIFTY) {
					score += Constants.THIRTY;
				}
			}

			if (Typology.CHALET.equals(ad.getTypology())) {
				if (wds.size() >= Constants.FIFTY) {
					score += Constants.TWENTY;
				}
			}

			if (wds.contains("luminoso"))
				score += Constants.FIVE;
			if (wds.contains("nuevo"))
				score += Constants.FIVE;
			if (wds.contains("céntrico"))
				score += Constants.FIVE;
			if (wds.contains("reformado"))
				score += Constants.FIVE;
			if (wds.contains("ático"))
				score += Constants.FIVE;
		}

		// Calcular puntuación por completitud
		if (ad.isComplete()) {
			score += Constants.FORTY;
		}

		ad.setScore(score);

		if (ad.getScore() < Constants.ZERO) {
			ad.setScore(Constants.ZERO);
		}

		if (ad.getScore() > Constants.ONE_HUNDRED) {
			ad.setScore(Constants.ONE_HUNDRED);
		}

		if (ad.getScore() < Constants.FORTY) {
			ad.setIrrelevantSince(new Date());
		} else {
			ad.setIrrelevantSince(null);
		}
	}
}
