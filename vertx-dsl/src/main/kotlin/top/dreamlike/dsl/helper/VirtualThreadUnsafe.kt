package top.dreamlike.helper

import sun.misc.Unsafe
import java.lang.invoke.LambdaMetafactory
import java.lang.invoke.MethodHandles
import java.lang.invoke.MethodType
import java.util.concurrent.Executor
import java.util.function.Supplier


object VirtualThreadUnsafe {
    val UNSAFE: Unsafe = unsafe
    val IMPL_LOOKUP = fetchUnsafeHandler()
    val VIRTUAL_THREAD_BUILDER = fetchVirtualThreadBuilder()
    private val CARRIERTHREAD_SUPPLIER: Supplier<Thread> = carrierThreadSupplier()
    private fun fetchUnsafeHandler(): MethodHandles.Lookup {
        val lookupClass = MethodHandles.Lookup::class.java
        return try {
            val implLookupField  = lookupClass.getDeclaredField("IMPL_LOOKUP")
            val offset: Long = UNSAFE.staticFieldOffset(implLookupField)
            UNSAFE.getObject(UNSAFE.staticFieldBase(implLookupField), offset) as MethodHandles.Lookup
        } catch (e: Exception) {
            throw RuntimeException(e)
        }
    }

    private fun fetchVirtualThreadBuilder(): java.util.function.Function<Executor, Thread.Builder.OfVirtual> {
        val tmp: Class<out Thread.Builder.OfVirtual> = Thread.ofVirtual().javaClass
        return try {
            val builderMethodHandle = IMPL_LOOKUP
                    .`in`(tmp)
                    .findConstructor(tmp, MethodType.methodType(Void.TYPE, Executor::class.java))
            val lambdaFactory = LambdaMetafactory.metafactory(
                    IMPL_LOOKUP,
                    "apply",
                    MethodType.methodType(java.util.function.Function::class.java),
                    MethodType.methodType(Any::class.java, Any::class.java),
                    builderMethodHandle,
                    builderMethodHandle.type()
            ).target
            lambdaFactory.invoke() as java.util.function.Function<Executor, Thread.Builder.OfVirtual>
        } catch (e: Throwable) {
            throw RuntimeException(e)
        }
    }

    private val unsafe: Unsafe
        private get() {
            val aClass: Class<Unsafe> = Unsafe::class.java
            return try {
                val unsafe = aClass.getDeclaredField("theUnsafe")
                unsafe.isAccessible = true
                unsafe.get(null) as Unsafe
            } catch (e: Exception) {
                throw RuntimeException(e)
            }
        }

    private fun carrierThreadSupplier(): Supplier<Thread> {
        return try {
            val currentCarrierThreadMh = IMPL_LOOKUP
                    .`in`(Thread::class.java)
                    .findStatic(Thread::class.java, "currentCarrierThread", MethodType.methodType(Thread::class.java))
            val lambda = LambdaMetafactory.metafactory(
                    IMPL_LOOKUP,
                    "get",
                    MethodType.methodType(Supplier::class.java),
                    MethodType.methodType(Any::class.java),
                    currentCarrierThreadMh,
                    currentCarrierThreadMh.type()
            ).target
            lambda.invoke() as Supplier<Thread>
        } catch (throwable: Throwable) {
            throw RuntimeException(throwable)
        }
    }

    fun currentCarrierThread(): Thread {
        val thread = Thread.currentThread()
        return if (thread.isVirtual) CARRIERTHREAD_SUPPLIER.get() else thread
    }
}