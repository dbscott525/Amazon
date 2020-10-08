package com.scott_tigers.oncall.utility;

public class LaunchAMSLTTRGraph extends Utility implements Command {

    public static void main(String[] args) throws Exception {
	new LaunchAMSLTTRGraph().run();
    }

    @Override
    public void run() throws Exception {
	LTTRPage
		.stream()
		.map(LTTRPage::getUrl)
		.forEach(this::launchUrl);
    }
}
