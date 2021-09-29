/*
 * Copyright 2019-2021 the original author or authors.
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

package org.vividus.softassert.model;

public class SoftAssertionError
{
    private final AssertionError error;
    private KnownIssue knownIssue;

    public SoftAssertionError(AssertionError error)
    {
        this.error = error;
    }

    public AssertionError getError()
    {
        return error;
    }

    public KnownIssue getKnownIssue()
    {
        return knownIssue;
    }

    public void setKnownIssue(KnownIssue knownIssue)
    {
        this.knownIssue = knownIssue;
    }

    public boolean isKnownIssue()
    {
        return knownIssue != null && !knownIssue.isPotentiallyKnown();
    }

    public boolean isFailTestCaseFast()
    {
        return isKnownIssue() && knownIssue.isFailTestCaseFast();
    }

    public boolean isFailTestSuiteFast()
    {
        return isKnownIssue() && knownIssue.isFailTestSuiteFast();
    }
}
