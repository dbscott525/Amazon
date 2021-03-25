package com.amazon.amsoperations.utility;

import java.util.List;

import com.amazon.amsoperations.bean.Engineer;
import com.amazon.amsoperations.shared.EngineerFiles;
import com.amazon.amsoperations.shared.Transform;

public class MergeAdds {

    public static void main(String[] args) {
	new MergeAdds().run();
    }

    private void run() {
	List<Engineer> newEngineers = EngineerFiles.ENGINEER_ADDS.readCSV();
	List<Engineer> exsitingEngineers = EngineerFiles.MASTER_LIST.readCSV();
	List<String> existingUIDs = Transform.list(exsitingEngineers, x -> x.map(Engineer::getUid));
	List<String> alreadyInList = Transform.list(newEngineers,
		t1 -> t1.map(Engineer::getUid).filter(x -> existingUIDs.contains(x)));
	if (alreadyInList.size() != 0) {
	    System.out.println("alreadyInList=" + (alreadyInList));
	    System.exit(1);
	}
	System.out.println("BEFORE exsitingEngineers=" + (exsitingEngineers));
	exsitingEngineers.addAll(newEngineers);
	System.out.println("AFTER exsitingEngineers=" + (exsitingEngineers));
	EngineerFiles.MASTER_LIST.replaceEngineerList(exsitingEngineers);
    }

}
