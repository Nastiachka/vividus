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

import static java.lang.String.format;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.openqa.selenium.Keys;
import org.openqa.selenium.Point;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Interactive;
import org.openqa.selenium.interactions.Sequence;
import org.vividus.selenium.IWebDriverProvider;
import org.vividus.steps.ui.validation.IBaseValidations;
import org.vividus.steps.ui.web.model.SequenceAction;
import org.vividus.steps.ui.web.model.SequenceActionType;
import org.vividus.ui.action.search.Locator;
import org.vividus.ui.web.action.search.WebLocatorType;

@ExtendWith(MockitoExtension.class)
class ActionStepsTests
{
    private static final String SIGNATURE_FIELD_XPATH = "//canvas";
    private static final String ELEMENT_EXISTS_MESSAGE = "Element for interaction";
    private static final String POINTER_MOVE_ACTION = "{duration=100, x=0, y=0, type=pointerMove, origin=Mock for "
            + "WebElement, hashCode: %d}";
    private static final String SEPARATOR = ", ";
    private static final String POINTER_MOVE = POINTER_MOVE_ACTION + SEPARATOR;
    private static final String TWICE_DURATION = "{duration=0, type=pause}, {duration=0, type=pause}, ";
    private static final String TEXT = "text";
    private static final String CLICK_ACTION = "{button=0, type=pointerDown}, {button=0, type=pointerUp}";
    private static final String DOUBLE_CLICK_ACTION = CLICK_ACTION + SEPARATOR + CLICK_ACTION;
    private static final String RELEASE_ACTION = "{button=0, type=pointerUp}";
    private static final String CLICK_AND_HOLD_ACTION = "{button=0, type=pointerDown}";
    private static final String COPY_SHORTCUT_KEY = "c";

    @Mock private IWebDriverProvider webDriverProvider;
    @Mock private IBaseValidations baseValidations;
    @InjectMocks private ActionSteps actionSteps;

    @Mock(extraInterfaces = Interactive.class)
    private WebDriver webDriver;

    @BeforeEach
    public void before()
    {
        when(webDriverProvider.get()).thenReturn(webDriver);
    }

    @SuppressWarnings({ "LineLength", "unchecked" })
    @Test
    void testExecuteSequenceOfActions()
    {
        var locator = new Locator(WebLocatorType.XPATH, SIGNATURE_FIELD_XPATH);
        var point = mock(Point.class);
        var webElement = mock(WebElement.class);
        int offset = 15;

        when(baseValidations.assertIfElementExists(ELEMENT_EXISTS_MESSAGE, locator)).thenReturn(webElement);
        when(point.getX()).thenReturn(offset);
        when(point.getY()).thenReturn(offset);

        var actions = List.of(
            new SequenceAction(SequenceActionType.DOUBLE_CLICK, locator),
            new SequenceAction(SequenceActionType.CLICK_AND_HOLD, locator),
            new SequenceAction(SequenceActionType.MOVE_BY_OFFSET, point),
            new SequenceAction(SequenceActionType.RELEASE, locator),
            new SequenceAction(SequenceActionType.ENTER_TEXT, TEXT),
            new SequenceAction(SequenceActionType.CLICK, locator),
            new SequenceAction(SequenceActionType.MOVE_TO, locator),
            new SequenceAction(SequenceActionType.DOUBLE_CLICK, null),
            new SequenceAction(SequenceActionType.CLICK_AND_HOLD, null),
            new SequenceAction(SequenceActionType.RELEASE, null),
            new SequenceAction(SequenceActionType.KEY_DOWN, List.of(Keys.COMMAND.name(), COPY_SHORTCUT_KEY)),
            new SequenceAction(SequenceActionType.KEY_UP, List.of(COPY_SHORTCUT_KEY, Keys.COMMAND.name())),
            new SequenceAction(SequenceActionType.KEY_DOWN, List.of(Keys.CONTROL.name())),
            new SequenceAction(SequenceActionType.PRESS_KEYS, List.of("v")),
            new SequenceAction(SequenceActionType.KEY_UP, List.of(Keys.CONTROL.name())),
            new SequenceAction(SequenceActionType.CLICK, null)
        );
        actionSteps.executeSequenceOfActions(actions);
        var hash = webElement.hashCode();
        var mouseSequence = "{id=default mouse, type=pointer, parameters={pointerType=mouse}, actions=["
                + format(POINTER_MOVE, hash)
                + DOUBLE_CLICK_ACTION + SEPARATOR
                + format(POINTER_MOVE, hash)
                + CLICK_AND_HOLD_ACTION + SEPARATOR
                + "{duration=200, x=15, y=15, type=pointerMove, origin=pointer}" + SEPARATOR
                + format(POINTER_MOVE, hash)
                + RELEASE_ACTION + SEPARATOR
                + TWICE_DURATION + TWICE_DURATION + TWICE_DURATION + TWICE_DURATION
                + format(POINTER_MOVE, hash)
                + CLICK_ACTION + SEPARATOR
                + format(POINTER_MOVE_ACTION, hash) + SEPARATOR
                + DOUBLE_CLICK_ACTION + SEPARATOR
                + CLICK_AND_HOLD_ACTION + SEPARATOR
                + RELEASE_ACTION + SEPARATOR
                + TWICE_DURATION + TWICE_DURATION + TWICE_DURATION + TWICE_DURATION
                + CLICK_ACTION
                + "]}";
        var keyboardSequence = "{type=key, actions=["
                + TWICE_DURATION + TWICE_DURATION + TWICE_DURATION + TWICE_DURATION + TWICE_DURATION
                + "{type=keyDown, value=t}, {type=keyUp, value=t}, {type=keyDown, value=e}, {type=keyUp, value=e}, "
                + "{type=keyDown, value=x}, {type=keyUp, value=x}, {type=keyDown, value=t}, {type=keyUp, value=t}, "
                + TWICE_DURATION + TWICE_DURATION + TWICE_DURATION + TWICE_DURATION + TWICE_DURATION
                + "{type=keyDown, value=" + Keys.COMMAND + "}, {type=keyDown, value=c}, "
                + "{type=keyUp, value=c}, {type=keyUp, value=" + Keys.COMMAND + "},"
                + " {type=keyDown, value=" + Keys.CONTROL + "}, {type=keyDown, value=v}, "
                + "{type=keyUp, value=v}, {type=keyUp, value=" + Keys.CONTROL + "}, "
                + "{duration=0, type=pause}, {duration=0, type=pause}], id=default keyboard}";
        var actionsCaptor = ArgumentCaptor.forClass(Collection.class);
        verify((Interactive) webDriver).perform(actionsCaptor.capture());
        assertEquals(mouseSequence + keyboardSequence, asString(actionsCaptor.getValue()));
    }

    @Test
    void testExecuteActionsSequenceElementIsNull()
    {
        var locator = new Locator(WebLocatorType.XPATH, SIGNATURE_FIELD_XPATH);

        when(baseValidations.assertIfElementExists(ELEMENT_EXISTS_MESSAGE, locator)).thenReturn(null);

        var actions = List.of(
            new SequenceAction(SequenceActionType.DOUBLE_CLICK, locator),
            new SequenceAction(SequenceActionType.ENTER_TEXT, TEXT)
        );
        actionSteps.executeSequenceOfActions(actions);
        verify((Interactive) webDriver, never()).perform(any());
    }

    private static String asString(Collection<Sequence> sequences)
    {
        return sequences.stream()
                .map(Sequence::encode)
                .map(Map::toString)
                .sorted()
                .collect(Collectors.joining());
    }
}
