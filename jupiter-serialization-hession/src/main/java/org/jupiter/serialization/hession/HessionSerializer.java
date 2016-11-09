/*
 * Copyright (c) 2015 The Jupiter Project
 *
 * Licensed under the Apache License, version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jupiter.serialization.hession;

import com.caucho.hessian.io.Hessian2Input;
import com.caucho.hessian.io.Hessian2Output;
import org.jupiter.common.util.internal.JUnsafe;
import org.jupiter.serialization.Serializer;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import static org.jupiter.serialization.SerializerType.HESSION;

/**
 * jupiter
 * org.jupiter.serialization.hession
 *
 * @author jiachun.fjc
 */
public class HessionSerializer implements Serializer {

    private final ThreadLocal<ByteArrayOutputStream> bufThreadLocal = new ThreadLocal<ByteArrayOutputStream>() {

        @Override
        protected ByteArrayOutputStream initialValue() {
            return new ByteArrayOutputStream(64);
        }
    };

    @Override
    public byte code() {
        return HESSION.value();
    }

    @Override
    public <T> byte[] writeObject(T obj) {
        ByteArrayOutputStream buf = bufThreadLocal.get();
        Hessian2Output out = new Hessian2Output(buf);
        try {
            out.writeObject(obj);
            out.flush();

            return buf.toByteArray();
        } catch (IOException e) {
            JUnsafe.throwException(e);
        } finally {
            buf.reset();
        }
        return null; // never get here
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T readObject(byte[] bytes, Class<T> clazz) {
        Hessian2Input input = new Hessian2Input(new ByteArrayInputStream(bytes));
        try {
            return (T) input.readObject(clazz);
        } catch (IOException e) {
            JUnsafe.throwException(e);
        }
        return null; // never get here
    }

    @Override
    public String toString() {
        return "hession:(code=" + code() + ")";
    }
}
