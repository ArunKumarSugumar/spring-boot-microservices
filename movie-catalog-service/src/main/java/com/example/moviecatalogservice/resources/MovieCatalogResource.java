package com.example.moviecatalogservice.resources;

import com.example.moviecatalogservice.models.CatalogItem;
import com.example.moviecatalogservice.models.Movie;
import com.example.moviecatalogservice.models.UserRating;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author ArunKumar.Sugumar
 */
@RestController
@RequestMapping("/catalog")
public class MovieCatalogResource {

    @Autowired
    private WebClient.Builder webClientBuilder;

    @RequestMapping("/{userId}")
    @HystrixCommand(fallbackMethod = "getFallBackCatalog")
    public List<CatalogItem> getCatalog(@PathVariable("userId") String userId) {

        UserRating ratings = webClientBuilder.build()
                .get()
                .uri("http://ratings-data-service/ratingsdata/users/" + userId)
                .retrieve()
                .bodyToMono(UserRating.class)
                .block();

        return ratings.getUserRatings().stream().map(rating -> {
            Movie movie = webClientBuilder.build()
                    .get()
                    .uri("http://movie-info-service/movies/" + rating.getMovieId())
                    .retrieve()
                    .bodyToMono(Movie.class)
                    .block();

            return new CatalogItem(movie.getName(), "Test", rating.getRating());
        }).collect(Collectors.toList());
    }

    public List<CatalogItem> getFallBackCatalog(@PathVariable("userId") String userId) {

        return Arrays.asList(new CatalogItem("No movie", "", 0));

    }
}
