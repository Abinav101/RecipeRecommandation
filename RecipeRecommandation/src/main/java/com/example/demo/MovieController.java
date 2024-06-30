package com.example.demo;


import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
//
//@Controller
//public class MovieController {
////    @PostMapping("/update/{id}")
////    public String updateMovie(@PathVariable Long id, @RequestParam("signupusername") String username, @RequestParam("signupuserId") String userId,) {
////        Movie movie = movieService.findById(id);
////        if (movie != null) {
////            movie.setTitle(updatedMovie.getTitle());
////            movie.setDescription(updatedMovie.getDescription());
////            movie.setImagePath(updatedMovie.getImagePath());
////            movieService.update(movie);
////            return "redirect:/movies/{id}";
////        } else {
////            // Handle movie not found
////            return "error";
////        }
////    }
//
//}
