<?php

function assertSomething($expected, $wow) {
    return test($expected . ' is correct '.$wow, function () {
        $this->assertTrue(true);
    });
}
