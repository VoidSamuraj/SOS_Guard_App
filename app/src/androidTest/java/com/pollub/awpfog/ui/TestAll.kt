package com.pollub.awpfog.ui

import com.pollub.awpfog.ui.components.EditGuardDataScreenTest
import com.pollub.awpfog.ui.components.InterventionButtonsTest
import com.pollub.awpfog.ui.components.InterventionSectionTest
import com.pollub.awpfog.ui.components.TopBarTest
import com.pollub.awpfog.ui.login.LoginScreenTest
import com.pollub.awpfog.ui.login.RegistrationScreenPersonalInformationTest
import com.pollub.awpfog.ui.login.RegistrationScreenTest
import com.pollub.awpfog.ui.login.RemindPasswordScreenTest
import org.junit.runner.RunWith
import org.junit.runners.Suite

@RunWith(Suite::class)
@Suite.SuiteClasses(
    EditGuardDataScreenTest::class,
    InterventionButtonsTest::class,
    InterventionSectionTest::class,
    TopBarTest::class,
    LoginScreenTest::class,
    RegistrationScreenTest::class,
    RegistrationScreenPersonalInformationTest::class,
    RemindPasswordScreenTest::class
)
class TestAll