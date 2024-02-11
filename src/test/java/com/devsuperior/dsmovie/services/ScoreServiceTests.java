package com.devsuperior.dsmovie.services;

import com.devsuperior.dsmovie.dto.MovieDTO;
import com.devsuperior.dsmovie.dto.ScoreDTO;
import com.devsuperior.dsmovie.entities.MovieEntity;
import com.devsuperior.dsmovie.entities.ScoreEntity;
import com.devsuperior.dsmovie.entities.UserEntity;
import com.devsuperior.dsmovie.repositories.MovieRepository;
import com.devsuperior.dsmovie.repositories.ScoreRepository;
import com.devsuperior.dsmovie.services.exceptions.ResourceNotFoundException;
import com.devsuperior.dsmovie.tests.MovieFactory;
import com.devsuperior.dsmovie.tests.ScoreFactory;
import com.devsuperior.dsmovie.tests.UserFactory;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
public class ScoreServiceTests {
	
	@InjectMocks
	private ScoreService service;

	@Mock
	private UserService userService;

	@Mock
	private MovieRepository movieRepository;

	@Mock
	private ScoreRepository scoreRepository;

	private Long existingMovieId, nonExistingMovieId;
	private UserEntity user;
	private MovieEntity movie;
	private ScoreEntity score;
	private ScoreDTO scoreDTO;

	@BeforeEach
	void setUp() {
		existingMovieId = 1L;
		nonExistingMovieId = 2L;

		user = UserFactory.createUserEntity();
		movie = MovieFactory.createMovieEntity();

		score = ScoreFactory.createScoreEntity();
		scoreDTO = ScoreFactory.createScoreDTO();

		when(movieRepository.findById(existingMovieId)).thenReturn(Optional.of(movie));
		when(movieRepository.findById(nonExistingMovieId)).thenReturn(Optional.empty());

		when(movieRepository.save(any())).thenReturn(movie);

		when(scoreRepository.saveAndFlush(any())).thenReturn(score);
	}

	@Test
	public void saveScoreShouldReturnMovieDTO() {
		when(userService.authenticated()).thenReturn(user);

		MovieDTO result = service.saveScore(scoreDTO);

		assertNotNull(result);
	}
	
	@Test
	public void saveScoreShouldThrowResourceNotFoundExceptionWhenNonExistingMovieId() {
		when(userService.authenticated()).thenReturn(user);

		movie.setId(nonExistingMovieId);
		score.setMovie(movie);
		movie.getScores().add(score);

		scoreDTO = new ScoreDTO(score);

		assertThrows(ResourceNotFoundException.class, () -> {
			service.saveScore(scoreDTO);
		});
	}
}
