/*
 * Copyright 2019-2022 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.vividus.steps.ui.web;

import javax.inject.Inject;

import org.jbehave.core.annotations.Then;
import org.jbehave.core.annotations.When;
import org.openqa.selenium.WebElement;
import org.vividus.steps.ui.web.validation.FocusValidations;
import org.vividus.ui.context.IUiContext;
import org.vividus.ui.monitor.TakeScreenshotOnFailure;
import org.vividus.ui.web.action.WebJavascriptActions;

@TakeScreenshotOnFailure
public class FocusSteps
{
    @Inject private IUiContext uiContext;
    @Inject private WebJavascriptActions javascriptActions;
    @Inject private FocusValidations focusValidations;

    /**
     * Step sets focus to an element in context
     * <p>
     * Actions performed at this step:
     * </p>
     * <ul>
     * <li>Sets focus by performing javascript method focus() to found element.</li>
     * </ul>
     */
    @When("I set focus to the context element")
    public void setFocus()
    {
        uiContext.getSearchContext(WebElement.class).ifPresent(
                elementToCheck -> javascriptActions.executeScript("arguments[0].focus()", elementToCheck));
    }

    /**
     * Step checks if the context element in given focus state
     * <p>
     * Actions performed at this step:
     * </p>
     * <ul>
     * <li>Gets element from context</li>
     * <li>Checks focus state of element by comparing given the element and the element
     *  returned by activeElement javascript method</li>
     * </ul>
     * @param focusState state to verify
     */
    @Then("the context element is $focusState")
    public void isElementInFocusState(FocusState focusState)
    {
        uiContext.getSearchContext(WebElement.class).ifPresent(
                elementToCheck -> focusValidations.isElementInFocusState(elementToCheck, focusState)
        );
    }
}
