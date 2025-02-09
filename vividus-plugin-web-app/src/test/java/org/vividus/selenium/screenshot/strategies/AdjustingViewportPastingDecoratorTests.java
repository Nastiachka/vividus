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

package org.vividus.selenium.screenshot.strategies;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;

import pazone.ashot.DebuggingViewportPastingDecorator;
import pazone.ashot.PageDimensions;
import pazone.ashot.util.InnerScript;

@ExtendWith(MockitoExtension.class)
class AdjustingViewportPastingDecoratorTests
{
    private static final int HEADER_ADJUSTMENT = 1;
    private static final int FOOTER_ADJUSTMENT = 2;

    private static final int WINDOW_HEIGHT = 500;

    private static final String SCROLL_Y_SCRIPT = "var scrY = window.scrollY;if(scrY){return scrY;} else {return 0;}";

    private AdjustingViewportPastingDecorator decorator;

    @Mock (extraInterfaces = JavascriptExecutor.class)
    private WebDriver webDriver;

    @BeforeEach
    void beforeEach()
    {
        decorator = new AdjustingViewportPastingDecorator(null, HEADER_ADJUSTMENT, FOOTER_ADJUSTMENT);
    }

    @Test
    void testOriginalWindowHeightIsAdjustedWithHeaderAndFooterCorrections()
    {
        try (MockedStatic<InnerScript> innerScriptMock = mockStatic(InnerScript.class))
        {
            innerScriptMock
                    .when(() -> InnerScript.execute(DebuggingViewportPastingDecorator.PAGE_DIMENSIONS_JS, webDriver))
                    .thenReturn(Map.of("pageHeight", 200, "viewportWidth", 150, "viewportHeight", WINDOW_HEIGHT));
            PageDimensions output = decorator.getPageDimensions(webDriver);
            assertEquals(WINDOW_HEIGHT - HEADER_ADJUSTMENT - FOOTER_ADJUSTMENT, output.getViewportHeight());
            assertEquals(200, output.getPageHeight());
            assertEquals(150, output.getViewportWidth());
        }
    }

    @Test
    void testNonAdjustedCurrentScrollYIsReturnedBeforeScrolling()
    {
        JavascriptExecutor jsExecutor = (JavascriptExecutor) webDriver;
        int currentScrollY = 0;
        when(jsExecutor.executeScript(SCROLL_Y_SCRIPT)).thenReturn(currentScrollY);
        assertEquals(currentScrollY, decorator.getCurrentScrollY(jsExecutor));
    }

    @Test
    void testAdjustedCurrentScrollYIsReturnedAfterSecondScrolling()
    {
        JavascriptExecutor jsExecutor = (JavascriptExecutor) webDriver;
        int currentScrollY = 100;
        when(jsExecutor.executeScript(SCROLL_Y_SCRIPT)).thenReturn(currentScrollY);
        decorator.getCurrentScrollY(jsExecutor);
        assertEquals(currentScrollY, decorator.getCurrentScrollY(jsExecutor));
        assertEquals(currentScrollY + HEADER_ADJUSTMENT, decorator.getCurrentScrollY(jsExecutor));
    }
}
