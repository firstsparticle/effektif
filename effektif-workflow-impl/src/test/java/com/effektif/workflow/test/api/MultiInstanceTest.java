/*
 * Copyright 2014 Effektif GmbH.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.effektif.workflow.test.api;

import org.junit.Test;

import com.effektif.workflow.api.activities.UserTask;
import com.effektif.workflow.api.model.TriggerInstance;
import com.effektif.workflow.api.model.UserId;
import com.effektif.workflow.api.types.ListType;
import com.effektif.workflow.api.types.UserIdType;
import com.effektif.workflow.api.workflow.MultiInstance;
import com.effektif.workflow.api.workflow.Workflow;
import com.effektif.workflow.api.workflowinstance.WorkflowInstance;
import com.effektif.workflow.impl.util.Lists;
import com.effektif.workflow.test.TestHelper;
import com.effektif.workflow.test.WorkflowTest;


/**
 * @author Tom Baeyens
 */
public class MultiInstanceTest extends WorkflowTest {
  
  @Test
  public void testMultiInstanceUserTask() throws Exception {
    Workflow workflow = new Workflow()
      .variable("reviewers", new ListType(new UserIdType()))
      .activity("Review", new UserTask()
        .assigneeExpression("reviewer")
        .multiInstance(new MultiInstance()
          .valuesExpression("reviewers")
          .variable("reviewer", new UserIdType())));
    
    deploy(workflow);
    
    WorkflowInstance workflowInstance = workflowEngine.start(new TriggerInstance()
      .workflowId(workflow.getId())
      .data("reviewers", Lists.of(
              new UserId("John"),
              new UserId("Jack"), 
              new UserId("Mary"))));

    // TODO make it so that the parent activity 
    // instance doesn't have a name and doesn't have the empty variable declaration
    TestHelper.assertOpen(workflowInstance, "Review", "Review", "Review", "Review");
  }
}
