package app.controller;

import java.security.Principal;
import java.util.List;
import java.util.Locale;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import app.dto.ActivityInfo;
import app.dto.CategoryInfo;
import app.dto.CustomUserDetails;
import app.service.ActivityService;
import app.service.BookService;
import app.service.CategoryService;

@Controller
public class HomeController {
	private static final Logger logger = Logger.getLogger(HomeController.class);
	@Autowired
	private BookService bookService;
	@Autowired
	CategoryService categoryService;

	@Autowired
	private ActivityService activityService;

	@Autowired
	private ReloadableResourceBundleMessageSource messageSource;

	@RequestMapping(value = "/")
	public ModelAndView index(Principal principal, Locale locale) {
		logger.info("home page");
		ModelAndView model = new ModelAndView("home");
		if (principal != null) {
			CustomUserDetails currentUser = (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
			String userName = principal.getName();
			List<ActivityInfo> activities = activityService.loadActivitiesByUserName(userName);
			if (activities.isEmpty()) {
				model.addObject("activityMsg", messageSource.getMessage("activities.empty", null, locale));
			}
			model.addObject("activities", activities);
			List<ActivityInfo> followerActivities = activityService.loadActivitiesFollowedByUserId(currentUser.getId());
			if(followerActivities.isEmpty()){
				model.addObject("followerActivitiesMsg", messageSource.getMessage("followerActivities.empty", null, locale));
			}
			model.addObject("followerActivities", followerActivities);
		}
		model.addObject("titles", bookService.getListTitle());
		model.addObject("categories", categoryService.categoryName());
		return model;
	}

	@RequestMapping(value = "/books", method = RequestMethod.GET)
	public ModelAndView loadbooks(@RequestParam(value = "page", required = false) Integer page) {
		logger.info("book page");
		ModelAndView model = new ModelAndView("books");
		int curentPage = 1;
		if (page != null)
			curentPage = page;
		model.addObject("curentPage", curentPage);
		model.addObject("titles", bookService.getListTitle());
		model.addObject("categories", categoryService.categoryName());
		model.addObject("page", bookService.page(bookService.count(), 8));
		model.addObject("books", bookService.listBookByPage(page));
		return model;
	}

	@RequestMapping(value = "/Search", method = RequestMethod.GET)
	public ModelAndView searchByTitle(@RequestParam("search") String key,
			@RequestParam("type-search") String typeSearch) {
		logger.info(typeSearch);
		String keyWords = key.trim();
		ModelAndView model = new ModelAndView("bookSearch");
		model.addObject("categories", categoryService.categoryName());
		model.addObject("titles", bookService.getListTitle());
		model.addObject("key", key);
		model.addObject("typeSearch", typeSearch);
		if (typeSearch.equals("title")) {
			model.addObject("books", bookService.findBookByTitle(keyWords));
		}
		if (typeSearch.equals("category")) {
			CategoryInfo category = categoryService.findCategoryByName(keyWords);
			model.addObject("books", bookService.listBooksByCategoryId(category.getId()));
		}
		return model;
	}
}
