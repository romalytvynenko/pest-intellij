<?php

function assertSomething($expected) {
    return test("$expected is correct", function () {
        $this->assertTrue(true);
    });
}

assertSomething('wow'); // wow is correct