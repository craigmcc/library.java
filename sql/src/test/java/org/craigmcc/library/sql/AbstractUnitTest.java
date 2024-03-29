/*
 * Copyright 2020 craigmcc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.craigmcc.library.sql;

import org.craigmcc.library.model.Constants;
import org.craigmcc.library.model.Model;
import org.junit.BeforeClass;
import org.mockito.Mockito;

import java.sql.Connection;
import java.sql.PreparedStatement;

public class AbstractUnitTest {

    protected static Connection connection = Mockito.mock(Connection.class);
    protected static final String MY_TABLE = "mytable";

    @BeforeClass
    public static void beforeClass() throws Exception {
        String[] keys = new String[] { "" };
        connection = Mockito.mock(Connection.class);
        Mockito.when(connection.prepareStatement("")).thenReturn(Mockito.mock(PreparedStatement.class));
        Mockito.when(connection.prepareStatement("", keys)).thenReturn(Mockito.mock(PreparedStatement.class));
    }

    static class ConcreteModel extends Model<ConcreteModel> implements Constants {

        ConcreteModel() {
        }

        ConcreteModel(String firstName, String lastName, Integer points) {
            this.firstName = firstName;
            this.lastName = lastName;
            this.points = points;
        }

        private String firstName;
        private String lastName;
        private Integer points;

        public String getFirstName() {
            return firstName;
        }

        public void setFirstName(String firstName) {
            this.firstName = firstName;
        }

        public String getLastName() {
            return lastName;
        }

        public void setLastName(String lastName) {
            this.lastName = lastName;
        }

        public Integer getPoints() {
            return points;
        }

        public void setPoints(Integer points) {
            this.points = points;
        }

        @Override
        public void copy(ConcreteModel that) {
            this.firstName = that.firstName;
            this.lastName = that.lastName;
            this.points = that.points;
        }
    }

}
