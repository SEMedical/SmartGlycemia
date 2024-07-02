package edu.tongji.backend.exception;

/*-
 * #%L
 * Tangxiaozhi
 * %%
 * Copyright (C) 2024 All contributors of the project
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #L%
 */





public class ExerciseException extends RuntimeException{
    public ExerciseException() {
        super();
    }

    public ExerciseException(String message) {
        super("Expected exercise exception under glycemia module:"+message);
    }

    public ExerciseException(String message, Throwable cause) {
        super("Expected chained exercise exception:"+message, cause);
    }

    public ExerciseException(Throwable cause) {
        super("Expected chained exercise exception:"+cause);
    }
}
