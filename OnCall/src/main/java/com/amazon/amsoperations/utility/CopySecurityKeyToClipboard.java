package com.amazon.amsoperations.utility;

import com.amazon.amsoperations.shared.Util;

public class CopySecurityKeyToClipboard extends Utility {

    public static void main(String[] args) {
	new CopySecurityKeyToClipboard().run();
    }

    private void run() {
	Util.copyToClipboard("Foi3i^CGD3");
	System.out.println("Security Key Copied to Clipboard");
    }
}
