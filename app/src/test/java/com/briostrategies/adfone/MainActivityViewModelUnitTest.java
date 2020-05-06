package com.briostrategies.adfone;

import com.google.android.gms.location.FusedLocationProviderClient;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.Assert.assertEquals;

@RunWith(JUnit4.class)
public class MainActivityViewModelUnitTest {

    @Mock
    private FusedLocationProviderClient mockClient;
    private MainActivityViewModel viewModel;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        viewModel = new MainActivityViewModel(mockClient);
    }

    @Test
    public void radiusCalculationTest() {
        assertEquals(8047, viewModel.calculateNewRadius(0));
        assertEquals(16094, viewModel.calculateNewRadius(1));
        assertEquals(24141, viewModel.calculateNewRadius(2));
        assertEquals(32188, viewModel.calculateNewRadius(3));
        assertEquals(40235, viewModel.calculateNewRadius(4));
        assertEquals(48282, viewModel.calculateNewRadius(5));
        assertEquals(50000, viewModel.calculateNewRadius(6));
    }

    @Test(expected = IllegalStateException.class)
    public void radiusCalculationNegativeTest() {
        viewModel.calculateNewRadius(-1);
    }
}
