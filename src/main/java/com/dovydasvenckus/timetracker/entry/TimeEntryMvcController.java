package com.dovydasvenckus.timetracker.entry;

import com.dovydasvenckus.timetracker.project.ProjectDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

import static java.util.Comparator.comparing;
import static org.springframework.beans.BeanUtils.copyProperties;

@Controller
public class TimeEntryMvcController {
    @Autowired
    TimeEntryRepository timeEntryRepository;

    @RequestMapping("/")
    public ModelAndView index() {
        ModelAndView viewModel = new ModelAndView();
        List<TimeEntry> timeEntries = timeEntryRepository.findAll();

        List<TimeEntryDTO> result = timeEntries.stream()
                .sorted(comparing(TimeEntry::getStartDate).reversed())
                .map((timeEntry) -> {
                    TimeEntryDTO timeEntryDTO = new TimeEntryDTO(timeEntry);

                    if (timeEntryDTO.getEndDate() != null) {
                        timeEntryDTO.setDifferenceInMinutes(ChronoUnit.MINUTES.between(timeEntry.getStartDate(), timeEntry.getEndDate()));
                    }
                    return timeEntryDTO;
                })
                .collect(Collectors.toList());

        viewModel.setViewName("index");
        viewModel.addObject("entries", result);

        return viewModel;
    }
}
