package com.cs203.grp2.Asg2.service;

import com.cs203.grp2.Asg2.DTO.*;
import com.cs203.grp2.Asg2.models.*;
import com.cs203.grp2.Asg2.exceptions.ConvertableNotFoundException;
import com.cs203.grp2.Asg2.exceptions.GeneralBadRequestException;
import com.google.firebase.database.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.*;
import java.util.concurrent.CompletableFuture;

@Service
public class ConvertableServiceImpl implements ConvertableService {

    @Autowired
    private FirebaseDatabase firebaseDatabase;

    private final List<Convertable> convertableList = new ArrayList<>();

    private ConvertableResponseDTO toDTO(Convertable c) {
        List<ConvertToResponseDTO> tos = new ArrayList<>();
        if (c.getTo() != null) {
            for (ConvertTo t : c.getTo()) {
                tos.add(new ConvertToResponseDTO(
                    t.getHscode(),
                    t.getName(),
                    t.getYield_percent()
                ));
            }
        }
        return new ConvertableResponseDTO(c.getHscode(), c.getName(), tos);
    }

    private void loadConvertables() throws Exception {
        DatabaseReference ref = firebaseDatabase.getReference("convertable").child("convertable");
        CompletableFuture<List<Convertable>> future = new CompletableFuture<>();
        convertableList.clear();

        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                try {
                    for (DataSnapshot snap : snapshot.getChildren()) {
                        DataSnapshot fromSnap = snap.child("from");
                        String hscode = fromSnap.child("hscode").getValue(String.class);
                        String name = fromSnap.child("name").getValue(String.class);

                        if (hscode == null || name == null) {
                            continue;
                        }

                        List<ConvertTo> tos = new ArrayList<>();
                        DataSnapshot toSnap = snap.child("to");
                        for (DataSnapshot child : toSnap.getChildren()) {
                            String toHscode = child.child("hscode").getValue(String.class);
                            String toName = child.child("name").getValue(String.class);
                            Integer yield = child.child("yield_percent").getValue(Integer.class);
                            if (toHscode != null && toName != null && yield != null) {
                                tos.add(new ConvertTo(toHscode, toName, yield));
                            }
                        }
                        convertableList.add(new Convertable(hscode, name, tos));
                    }
                    future.complete(convertableList);
                } catch (Exception e) {
                    future.completeExceptionally(e);
                }
            }
            @Override
            public void onCancelled(DatabaseError error) {
                future.completeExceptionally(error.toException());
            }
        });
        future.get();
    }

    @Override
    public List<ConvertableResponseDTO> getAllConvertables() {
        try {
            loadConvertables();
            List<ConvertableResponseDTO> dtos = new ArrayList<>();
            for (Convertable c : convertableList) {
                dtos.add(toDTO(c));
            }
            return dtos;
        } catch (Exception e) {
            throw new GeneralBadRequestException("Error loading convertables: " + e.getMessage());
        }
    }

    @Override
    public ConvertableResponseDTO getConvertableByHscode(String hscode) {
        try {
            loadConvertables();
            for (Convertable c : convertableList) {
                if (c.getHscode().equals(hscode)) {
                    return toDTO(c);
                }
            }
            throw new ConvertableNotFoundException(hscode);
        } catch (ConvertableNotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new GeneralBadRequestException("Error loading convertable: " + e.getMessage());
        }
    }
}