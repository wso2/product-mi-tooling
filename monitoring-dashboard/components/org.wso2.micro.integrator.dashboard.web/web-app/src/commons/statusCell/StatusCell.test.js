import React from 'react';
import renderer from 'react-test-renderer';
import StatusCell from "./StatusCell";

test('Test on green batch', () => {
    const component = renderer.create(
        <StatusCell data={"Active"}/>,
    );
    let tree = component.toJSON();
    expect(tree).toMatchSnapshot();

});

test('Test on red batch', () => {
    const component = renderer.create(
        <StatusCell data={"Inactive"}/>,
    );
    let tree = component.toJSON();
    expect(tree).toMatchSnapshot();

});
