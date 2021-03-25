package com.amazon.amsoperations.utility;

import java.util.Optional;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

public class LTTRWebRow {

    private String ticketId;

    public LTTRWebRow(WebElement row) {

	ticketId = Optional
		.ofNullable(row)
		.map(r -> r.findElements(By.tagName("td")))
		.filter(td -> td.size() > 0)
		.map(td -> td.get(0))
		.map(cell -> cell.getText())
		.orElse("");
    }

    public String getTicketId() {
	return ticketId;
    }

    @Override
    public String toString() {
	return "LTTRWebRow [ticketId=" + ticketId + "]";
    }

}
