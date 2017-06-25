package com.ondrejhrusovsky.suitetest;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import com.ondrejhrusovsky.test.FormularVyhladatSpojenieTest;
import com.ondrejhrusovsky.test.StrankaSpojeTest;
import com.ondrejhrusovsky.test.StrankaVyhladanieSpojaTest;

@RunWith(Suite.class)
@Suite.SuiteClasses({
        StrankaSpojeTest.class
})

public class StrankaSpojeSuiteTest {

}
