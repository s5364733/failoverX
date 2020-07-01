/**
 * The MIT License
 *
 * Copyright for portions of failover-safe are held by creatorchina Inc (c) 2020.
 *
 * Permission is hereby granted, free of charge, to any person obtaining
 * a copy of this software and associated documentation files (the
 * "Software"), to deal in the Software without restriction, including
 * without limitation the rights to use, copy, modify, merge, publish,
 * distribute, sublicense, and/or sell copies of the Software, and to
 * permit persons to whom the Software is furnished to do so, subject to
 * the following conditions:
 *
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
 * LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION
 * OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION
 * WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */


package com.creatorchina.json;

import net.jodah.failsafe.Policy;

import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @ClassName: JSONWriter
 * @Description: $
 * @Author: jack.liang
 * @Date: 2020/6/30 上午2:42
 **/
public class JSONWriter {

    private StringBuilder    out;

    public JSONWriter(){
        this.out = new StringBuilder();
    }


    public void writeNull() {
        write("null");
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    public void writeObject(Object o) {
        if (o == null) {
            writeNull();
            return;
        }

        if (o instanceof String) {
            writeString((String) o);
            return;
        }

        if (o instanceof Number) {
            write(o.toString());
            return;
        }

        if (o instanceof Boolean) {
            write(o.toString());
            return;
        }

        if (o instanceof Date) {
            writeDate((Date) o);
            return;
        }

        if (o instanceof Collection) {
            writeArray((Collection) o);
            return;
        }

        if (o instanceof Throwable) {
            writeError((Throwable) o);
            return;
        }

        if (o instanceof int[]) {
            int[] array = (int[]) o;
            write('[');
            for (int i = 0; i < array.length; ++i) {
                if (i != 0) {
                    write(',');
                }
                write(array[i]);
            }
            write(']');
            return;
        }

        if (o instanceof long[]) {
            long[] array = (long[]) o;
            write('[');
            for (int i = 0; i < array.length; ++i) {
                if (i != 0) {
                    write(',');
                }
                write(array[i]);
            }
            write(']');
            return;
        }

        if (o instanceof Policy[]) {
            Policy[] array = (Policy[]) o;
            write('[');
            for (int i = 0; i < array.length; ++i) {
                if (i != 0) {
                    write(',');
                }
                write(array[i].toString());
            }
            write(']');
            return;
        }

        throw new IllegalArgumentException("not support type : " + o.getClass());
    }

    public void writeDate(Date date) {
        if (date == null) {
            writeNull();
            return;
        }
        //SimpleDataFormat is not thread-safe, we need to make it local.
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        writeString(dateFormat.format(date));
    }

    public void writeError(Throwable error) {
        if (error == null) {
            writeNull();
            return;
        }

        write("{\"Class\":");
        writeString(error.getClass().getName());
        write(",\"Message\":");
        writeString(error.getMessage());
        write(",\"StackTrace\":");
        writeString((error.getMessage()));
        write('}');
    }

    public void writeArray(Object[] array) {
        if (array == null) {
            writeNull();
            return;
        }

        write('[');

        for (int i = 0; i < array.length; ++i) {
            if (i != 0) {
                write(',');
            }
            writeObject(array[i]);
        }

        write(']');
    }

    public void writeArray(Collection<Object> list) {
        if (list == null) {
            writeNull();
            return;
        }

        int entryIndex = 0;
        write('[');

        for (Object entry : list) {
            if (entryIndex != 0) {
                write(',');
            }
            writeObject(entry);
            entryIndex++;
        }

        write(']');
    }

    public void writeString(String text) {
        if (text == null) {
            writeNull();
            return;
        }

        write('"');
        for (int i = 0; i < text.length(); ++i) {
            char c = text.charAt(i);
            if (c == '"') {
                write("\\\"");
            } else if (c == '\n') {
                write("\\n");
            } else if (c == '\r') {
                write("\\r");
            } else if (c == '\\') {
                write("\\\\");
            } else if (c == '\t') {
                write("\\t");
            } else if (c < 16) {
                write("\\u000");
                write(Integer.toHexString(c));
            } else if (c < 32) {
                write("\\u00");
                write(Integer.toHexString(c));
            } else if (c >= 0x7f && c <= 0xA0) {
                write("\\u00");
                write(Integer.toHexString(c));
            } else {
                write(c);
            }
        }
        write('"');
    }

    public void writeMap(Map<String, Object> map) {
        if (map == null) {
            writeNull();
            return;
        }

        int entryIndex = 0;
        write('{');
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            if (entryIndex != 0) {
                write(',');
            }

            writeString(entry.getKey());
            write(':');
            writeObject(entry.getValue());

            entryIndex++;
        }

        write('}');
    }

    protected void write(String text) {
        out.append(text);
    }

    protected void write(char c) {
        out.append(c);
    }

    protected void write(int c) {
        out.append(c);
    }

    protected void write(long c) {
        out.append(c);
    }

    @Override
    public String toString() {
        return out.toString();
    }
}
