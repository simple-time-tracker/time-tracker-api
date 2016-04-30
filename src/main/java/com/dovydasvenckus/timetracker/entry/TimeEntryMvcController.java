package com.dovydasvenckus.timetracker.entry;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

import static java.util.Comparator.comparing;

@Controller
public class TimeEntryMvcController {
    @Autowired
    TimeEntryRepository timeEntryRepository;

    @RequestMapping("/")
    public ModelAndView index() {
        ModelAndView viewModel = new ModelAndView();
        List<TimeEntry> timeEntries = timeEntryRepository.findAll();

        timeEntries = timeEntries.stream()
                .sorted(comparing(TimeEntry::getStartDate).reversed())
                .map((te) -> {
                    if (te.getEndDate() != null) {
                        te.setDifferenceInMinutes(ChronoUnit.MINUTES.between(te.getStartDate(), te.getEndDate()));
                    }
                    return te;
                })
                .collect(Collectors.toList());

        viewModel.setViewName("index");
        viewModel.addObject("entries", timeEntries);

        return viewModel;
    }
}
