<template>
    <div>
        <Form :label-width="80">
            <FormItem label="DEL Quote ID">
                <Input v-model="quote.id"></Input>
            </FormItem>
        </Form>
    </div>
</template>

<script>
    export default {
        name: "del-upload",
        data() {
            return {
                quote: {}
            }
        }
    }
</script>

<style scoped>

</style>
