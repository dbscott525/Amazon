package com.amazon.amsoperations.utility;

import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;

import com.amazon.amsoperations.shared.Dates;
import com.amazon.amsoperations.shared.EngineerFiles;
import com.amazon.amsoperations.shared.URL;
import com.amazon.amsoperations.shared.Util;

public class LTTRWeeks extends Utility {

    private static List<String> weeks = null;

    public static List<String> get() {
	return new LTTRWeeks().getCurrentWeeks();
    }

    private List<String> getCurrentWeeks() {
	weeks = Stream.of(fromMemory(), fromDisk(), fromLTTR())
		.map(Supplier<List<String>>::get)
		.filter(Objects::nonNull)
		.findFirst()
		.orElseThrow();
	return weeks;
    }

    private Supplier<List<String>> fromMemory() {
	return () -> weeks;
    }

    private Supplier<List<String>> fromDisk() {
	return () -> EngineerFiles.LTTR_WEEKS.readJson(PersistedWeeks.class).getCurrentWeeks();
    }

    private Supplier<List<String>> fromLTTR() {
	return () -> {
	    List<String> weeks = getWeeksFromLTTR();
	    EngineerFiles.LTTR_WEEKS.writeJson(new PersistedWeeks(weeks));
	    return weeks;
	};
    }

    private List<String> getWeeksFromLTTR() {
	WebDriver driver = Util.getWebDriver();
	driver.get(URL.LTTR_PRIORITY);

	WebElement week = driver.findElement(By.id("week"));
	Select dropdown = new Select(week);

	List<String> weeks = dropdown
		.getOptions()
		.stream()
		.map(WebElement::getText)
		.collect(Collectors.toList());

	driver.quit();

	return weeks;
    }

    private class PersistedWeeks {
	private String date;
	private List<String> weeks;

	public PersistedWeeks(List<String> weeks) {
	    this.date = Dates.SORTABLE.getFormattedDate();
	    this.weeks = weeks;
	}

	public List<String> getCurrentWeeks() {
	    return Dates.SORTABLE.getFormattedDate().equals(date) ? weeks : null;
	}

    }

}
