package com.cognifide.apmt.tests.page

import com.cognifide.apmt.TestCase
import com.cognifide.apmt.User
import com.cognifide.apmt.actions.Action
import com.cognifide.apmt.actions.page.CreatePage
import com.cognifide.apmt.actions.page.OpenPage
import com.cognifide.apmt.config.ConfigurationProvider
import com.cognifide.apmt.config.Instance
import com.cognifide.apmt.tests.Allowed
import com.cognifide.apmt.tests.ApmtBaseTest
import com.cognifide.apmt.tests.Denied
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.params.ParameterizedTest

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DisplayName("Check user permissions to open pages")
abstract class OpenPageTest(
    vararg testCases: TestCase,
    private var instance: Instance = ConfigurationProvider.authorInstance
) : ApmtBaseTest(*testCases) {

    private var undoAction: Action? = null

    @DisplayName("User can open pages")
    @ParameterizedTest
    @Allowed
    fun userCanOpenPages(user: User, path: String) {
        if (instance == ConfigurationProvider.authorInstance) {
            undoAction = CreatePage(instance, ConfigurationProvider.adminUser, path)
            undoAction?.execute()
        }

        OpenPage(instance, user, path)
            .execute()
            .then()
            .assertThat()
            .statusCode(200)
    }

    @DisplayName("User cannot open pages")
    @ParameterizedTest
    @Denied
    fun userCannotOpenPages(user: User, path: String) {
        if (instance == ConfigurationProvider.authorInstance) {
            undoAction = CreatePage(instance, ConfigurationProvider.adminUser, path)
            undoAction?.execute()
        }

        OpenPage(instance, user, path)
            .execute()
            .then()
            .assertThat()
            .statusCode(404)
    }

    @BeforeEach
    fun init() {
        undoAction = null
    }

    @AfterEach
    fun cleanup() {
        undoAction?.undo()
    }
}