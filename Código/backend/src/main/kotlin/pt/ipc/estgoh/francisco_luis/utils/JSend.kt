/*
 * MIT License
 *
 * Copyright (c) 2019 Murry Jeong (comchangs@gmail.com)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package pt.ipc.estgoh.francisco_luis.utils

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.serialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import pt.ipc.estgoh.francisco_luis.data.Category
import pt.ipc.estgoh.francisco_luis.data.Item
import pt.ipc.estgoh.francisco_luis.data.ListItem
import pt.ipc.estgoh.francisco_luis.data.User
import java.util.*

/**
 * JSend
 * A library used for generating Json String of JSend
 *
 * @author Murry Jeong (comchangs@gmail.com)
 */
object ObjSerializer : KSerializer<Any> {
    override fun deserialize(decoder: Decoder): Any {
        TODO("Not yet implemented")
    }

    override val descriptor: SerialDescriptor
        get() = serialDescriptor<Item>()

    override fun serialize(encoder: Encoder, value: Any) {

        when (value) {
            is List<*> -> {
                if (value.isNotEmpty()) {
                    if (value[0] is Item)
                        ListSerializer(Item.serializer()).serialize(encoder, value as List<Item>)
                    else if (value[0] is pt.ipc.estgoh.francisco_luis.data.List)
                        ListSerializer(pt.ipc.estgoh.francisco_luis.data.List.serializer())
                            .serialize(encoder, value as List<pt.ipc.estgoh.francisco_luis.data.List>)
                    else if (value[0] is Item)
                        ListSerializer(Item.serializer()).serialize(encoder, value as List<Item>)
                    else if (value[0] is Category)
                        ListSerializer(Category.serializer()).serialize(encoder,value as List<Category>)
                }
            }

            is String -> {
                encoder.encodeString(value)
            }

            is User -> {
                User.serializer().serialize(encoder,value)
            }

            is ListItem -> {
                ListItem.serializer().serialize(encoder,value)
            }

            is Item -> {
                Item.serializer().serialize(encoder,value)
            }

            is pt.ipc.estgoh.francisco_luis.data.List -> {
                pt.ipc.estgoh.francisco_luis.data.List.serializer().serialize(encoder,value)
            }
        }

    }

}


@Serializable
class JSend(
    private val status: JSendStatus,
    private val code: Int?,
    private val message: String?,
    @Serializable(with = ObjSerializer::class)
    private val data: Any?
) {
    /**
     * toString
     *
     * @return String
     */
    override fun toString(): String {
        return StringJoiner(", ", JSend::class.java.simpleName + "[", "]")
            .add("status=$status")
            .add("code=$code")
            .add("message='$message'")
            .add("data='$data'")
            .toString()
    }

    companion object {
        /**
         * success without data
         *
         * @return JSend
         */
        fun success(data: Any?): JSend {
            return JSend(JSendStatus.success, null, null, data)
        }

        fun success(code: Int?, data: Any?): JSend {
            return JSend(JSendStatus.success, code, null, data)
        }

        /**
         * error with code, message and data
         *
         * @param code status code for response
         * @param message An error message
         * @param data An object for data in response
         * @return JSend
         */
        fun error(code: Int?, message: String?, data: Any?): JSend {
            return JSend(JSendStatus.error, code, message, data)
        }

        /**
         * error with message only
         *
         * @param message An error message
         * @return JSend
         */
        fun error(message: String?): JSend {
            return error(null, message, null)
        }

        /**
         * fail with data
         *
         * @param data An object for data in response
         * @return Jsend
         */
        fun fail(data: Any?): JSend {
            return JSend(JSendStatus.fail, null, null, data)
        }
    }
}